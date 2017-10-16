from appconfig import status_codes, format_time, get_db_cursor
import psycopg2
import psycopg2.extras


INSERT_POSTS_SQL = \
    """INSERT INTO posts (author, created, forum, id, message, parent, thread, path, root_id) VALUES %s"""

GET_PATH_SQL = """SELECT path FROM posts WHERE id = %(parent)s"""

GET_POST_SQL = """SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE id = %(id)s"""

UPDATE_POSTS_ON_FORUM_SQL = """UPDATE forums SET posts = posts + %(amount)s WHERE slug = %(forum)s"""

UPDATE_POST_SQL = """UPDATE posts SET message = %(message)s, isEdited = TRUE WHERE id = %(id)s RETURNING 
						author, created, forum, id, isEdited, message, parent, thread"""



def posts_flat_sort_sql(slug_or_id, desc):
    sql = "SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE thread = "
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    sql += " ORDER BY created"
    if desc:
        sql += " DESC"
    sql += ", id"
    if desc:
        sql += " DESC"
    sql += " LIMIT %(limit)s"
    return sql

def posts_flat_sort_since_sql(slug_or_id, desc):
    sql = "SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE thread = "
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    sql += " ORDER BY created"
    if desc:
        sql += " DESC"
    sql += ", id"
    if desc:
        sql += " DESC"
    return sql

def posts_tree_sort_sql(slug_or_id, desc):
    sql = "SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE thread = "
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    sql += " ORDER BY path"
    if desc:
        sql += " DESC"
    sql += " LIMIT %(limit)s"
    return sql

def posts_tree_sort_since_sql(slug_or_id, desc):
    sql = "SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE thread = "
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    sql += " ORDER BY path"
    if desc:
        sql += " DESC"
    return sql


def posts_parent_tree_sort_sql(slug_or_id, desc):
    sql = """SELECT author, created, forum, id, isEdited, message, parent, thread 
				FROM posts 
				WHERE root_id IN (
					SELECT id
					FROM posts
					WHERE thread = """
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    sql += " AND parent = 0 ORDER BY id"
    if desc:
        sql += " DESC"
    sql += " LIMIT %(limit)s OFFSET %(since)s)"
    sql += " ORDER BY path"
    if desc:
        sql += " DESC"
    return sql

def posts_parent_tree_sort_since_sql(slug_or_id, desc):
    sql = """SELECT author, created, forum, id, isEdited, message, parent, thread 
				FROM posts 
				WHERE root_id IN (
					SELECT id
					FROM posts
					WHERE thread = """
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    sql += " AND parent = 0 ORDER BY id"
    if desc:
        sql += " DESC"

    sql += ") ORDER BY path"
    if desc:
        sql += " DESC"
    return sql


def posts_since_limit_parent (content, since, limit):
    for i in range(len(content) - 1):
        if content[i]["id"] == since:
            start = i + 1
            stop = start
            flag = 1
            for j in range (start, len(content)-1):
                if content[j]["parent"] == "0":
                    flag += 1
                if flag > limit:
                    break
                stop += 1
            return content[start:stop+2]
    return []

def posts_since_limit (content, since, limit):
    for i in range(len(content) - 1):
        if content[i]["id"] == since:
            return content[i+1:i+limit+1]
    return []


class PostsDb:

    @staticmethod
    def get_id():
        content = None
        try:
            with get_db_cursor() as cursor:
                cursor.execute("SELECT nextval('posts_id_seq')")
                content = cursor.fetchone()
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
        return content['nextval']

    @staticmethod
    def set_id(identifier):
        try:
            with get_db_cursor() as cursor:
                cursor.execute("SELECT setval('posts_id_seq', %(id)s, false)", {'id': identifier})
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)

    @staticmethod
    def count():
        content = None
        try:
            with get_db_cursor() as cursor:
                cursor.execute("SELECT COUNT(*) FROM posts")
                content = cursor.fetchone()
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
        return content['count']

    @staticmethod
    def get_path(parent):
        content = None
        try:
            with get_db_cursor() as cursor:
                cursor.execute(GET_PATH_SQL, {'parent': parent})
                content = cursor.fetchone()
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
        return content['path']

    @staticmethod
    def create(data, forum):
        code = status_codes['CREATED']
        try:
            with get_db_cursor(commit=True) as cursor:
                psycopg2.extras.execute_values(cursor, INSERT_POSTS_SQL, data)
                cursor.execute(UPDATE_POSTS_ON_FORUM_SQL, {'amount': len(data), 'forum': forum})
        except psycopg2.IntegrityError as e:
            print('Error %s' % e)
            code = status_codes['NOT_FOUND']
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
            code = status_codes['NOT_FOUND']
        return code

    @staticmethod
    def update(identifier, content):
        code = status_codes['OK']
        try:
            with get_db_cursor(commit=True) as cursor:
                cursor.execute(UPDATE_POST_SQL, {'message': content['message'], 'id': identifier})
                content = cursor.fetchone()
                if content is None:
                    code = status_codes['NOT_FOUND']
                else:
                    content['created'] = format_time(content['created'])
                    content['isEdited'] = content['isedited']
                    del content['isedited']
        except psycopg2.IntegrityError as e:
            print('Error %s' % e)
            code = status_codes['CONFLICT']
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
            code = status_codes['NOT_FOUND']
        return content, code

    @staticmethod
    def get(identifier):
        content = None
        code = status_codes['OK']
        try:
            with get_db_cursor() as cursor:
                cursor.execute(GET_POST_SQL, {'id': identifier})
                content = cursor.fetchone()
                if content is None:
                    code = status_codes['NOT_FOUND']
                else:
                    content['created'] = format_time(content['created'])
                    content['isEdited'] = content['isedited']
                    del content['isedited']
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
        return content, code

    @staticmethod
    def sort(limit, since, sort, desc, slug_or_id):
        content = None
        code = status_codes['OK']
        try:
            with get_db_cursor() as cursor:
                params = {'slug_or_id': slug_or_id, 'limit': limit, 'since': since}
                if sort == 'flat':
                    if since == 0:
                        cursor.execute(posts_flat_sort_sql(slug_or_id=slug_or_id, desc=desc), params)
                        content = cursor.fetchall()
                    else:
                        cursor.execute(posts_flat_sort_since_sql(slug_or_id=slug_or_id, desc=desc), params)
                        content = cursor.fetchall()
                        content = posts_since_limit (content, int(since), int(limit))
                elif sort == 'tree':
                    if since == 0:
                        cursor.execute(posts_tree_sort_sql(slug_or_id=slug_or_id, desc=desc), params)
                        content = cursor.fetchall()
                    else:
                        cursor.execute(posts_tree_sort_since_sql(slug_or_id=slug_or_id, desc=desc), params)
                        content = cursor.fetchall()
                        content = posts_since_limit (content, int(since), int(limit))
                elif sort == 'parent_tree':
                    if since == 0:
                        cursor.execute(posts_parent_tree_sort_sql(slug_or_id=slug_or_id, desc=desc), params)
                        content = cursor.fetchall()
                    else:
                        cursor.execute(posts_parent_tree_sort_since_sql(slug_or_id=slug_or_id, desc=desc), params)
                        content = cursor.fetchall()
                        content = posts_since_limit_parent(content, int(since), int(limit))

            for param in content:
                param['created'] = format_time(param['created'])
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
        return content, code

    @staticmethod
    def clear():
        try:
            with get_db_cursor(commit=True) as cursor:
                cursor.execute("DELETE FROM posts")
        except psycopg2.DatabaseError as e:
            print('Error %s' % e)
