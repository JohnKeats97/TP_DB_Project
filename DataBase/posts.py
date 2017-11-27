from appconfig import status_codes, format_time, get_db_cursor
import psycopg2
import psycopg2.extras


def posts_flat_sort_sql(slug_or_id, limit, since, desc):

    sql = "SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE thread = "
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    order = (" DESC " if (desc is True) else " ASC ")
    sign = (" < " if (desc is True) else " > ")
    if (since != None):
        sql += " AND id" + sign + "%(since)s "
    sql += "ORDER BY id " + order
    if (limit != None):
        sql += "LIMIT %(limit)s"

    return sql



def posts_tree_sort_sql(slug_or_id, limit, since, desc):
    sql = "SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE thread = "
    if slug_or_id.isdigit():
        sql += "%(slug_or_id)s"
    else:
        sql += "(SELECT id FROM threads WHERE slug = %(slug_or_id)s)"
    order = (" DESC " if (desc is True) else " ASC ")
    sign = (" < " if (desc is True) else " > ")
    if (since != None):
        sql += " AND path" + sign + "(SELECT path FROM posts WHERE id = %(since)s) "
    sql += "ORDER BY path " + order
    if (limit != None):
        sql += "LIMIT %(limit)s"

    return sql



def posts_parent_tree_sort_sql(slug_or_id, limit, since, desc):
    order = (" DESC " if (desc is True) else " ASC ")
    sign = (" < " if (desc is True) else " > ")
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
    sql += " AND parent = 0 "

    if (since != None):
        sql += " AND path " + sign + "(SELECT path FROM posts WHERE id = %(since)s) "
    sql += " ORDER BY id " + order

    if (limit != None):
        sql += "LIMIT %(limit)s"
    sql += ") "
    sql += " ORDER BY path " + order

    return sql

def insert_posts_sql():
    return """INSERT INTO posts (author, created, forum, id, message, parent, thread, path, root_id) VALUES %s"""

def get_post_sql():
    return """SELECT author, created, forum, id, isEdited, message, parent, thread FROM posts WHERE id = %(id)s"""

def get_path_sql():
    return """SELECT path FROM posts WHERE id = %(parent)s"""

def update_post_sql():
    return """UPDATE posts SET message = %(message)s, isEdited = TRUE WHERE id = %(id)s RETURNING 
						author, created, forum, id, isEdited, message, parent, thread"""

def update_posts_on_forum_sql():
    return """UPDATE forums SET posts = posts + %(amount)s WHERE slug = %(forum)s"""

class PostsDb:

    @staticmethod
    def set_id(identifier):
        try:
            with get_db_cursor() as cursor:
                cursor.execute("SELECT setval('posts_id_seq', %(id)s, false)", {'id': identifier})
        except psycopg2.DatabaseError as e:
            print('Error')

    @staticmethod
    def get_id():
        content = None
        try:
            with get_db_cursor() as cursor:
                cursor.execute("SELECT nextval('posts_id_seq')")
                content = cursor.fetchone()
        except psycopg2.DatabaseError as e:
            print('Error')
        return content['nextval']

    @staticmethod
    def count():
        content = None
        try:
            with get_db_cursor() as cursor:
                cursor.execute("SELECT COUNT(*) FROM posts")
                content = cursor.fetchone()
        except psycopg2.DatabaseError as e:
            print('Error')
        return content['count']

    @staticmethod
    def get_path(parent):
        content = None
        try:
            with get_db_cursor() as cursor:
                cursor.execute(get_path_sql(), {'parent': parent})
                content = cursor.fetchone()
        except psycopg2.DatabaseError as e:
            print('Error')
        return content['path']

    @staticmethod
    def get(identifier):
        content = None
        code = status_codes['OK']
        try:
            with get_db_cursor() as cursor:
                cursor.execute(get_post_sql(), {'id': identifier})
                content = cursor.fetchone()
                if content is None:
                    code = status_codes['NOT_FOUND']
                else:
                    content['created'] = format_time(content['created'])
                    content['isEdited'] = content['isedited']
                    del content['isedited']
        except psycopg2.DatabaseError as e:
            print('Error')
        return content, code

    @staticmethod
    def create(data, forum):
        code = status_codes['CREATED']
        try:
            with get_db_cursor(commit=True) as cursor:
                psycopg2.extras.execute_values(cursor, insert_posts_sql(), data)
                cursor.execute(update_posts_on_forum_sql(), {'amount': len(data), 'forum': forum})
        except psycopg2.IntegrityError as e:
            code = status_codes['NOT_FOUND']
        except psycopg2.DatabaseError as e:
            code = status_codes['NOT_FOUND']
        return code

    @staticmethod
    def update(identifier, content):
        code = status_codes['OK']
        try:
            with get_db_cursor(commit=True) as cursor:
                cursor.execute(update_post_sql(), {'message': content['message'], 'id': identifier})
                content = cursor.fetchone()
                if content is None:
                    code = status_codes['NOT_FOUND']
                else:
                    content['created'] = format_time(content['created'])
                    content['isEdited'] = content['isedited']
                    del content['isedited']
        except psycopg2.IntegrityError as e:
            code = status_codes['CONFLICT']
        except psycopg2.DatabaseError as e:
            code = status_codes['NOT_FOUND']
        return content, code

    @staticmethod
    def sort(limit, since, sort, desc, slug_or_id):
        content = None
        code = status_codes['OK']
        try:
            with get_db_cursor() as cursor:
                params = {'slug_or_id': slug_or_id, 'limit': limit, 'since': since}
                if sort == 'flat':
                    cursor.execute(posts_flat_sort_sql(slug_or_id=slug_or_id, limit=limit, since=since, desc=desc), params)
                elif sort == 'tree':
                    cursor.execute(posts_tree_sort_sql(slug_or_id=slug_or_id, limit=limit, since=since, desc=desc), params)
                elif sort == 'parent_tree':
                    cursor.execute(posts_parent_tree_sort_sql(slug_or_id=slug_or_id, limit=limit, since=since, desc=desc), params)
                content = cursor.fetchall()
            for param in content:
                param['created'] = format_time(param['created'])
        except psycopg2.DatabaseError as e:
            print('Error')
        return content, code

    @staticmethod
    def clear():
        try:
            with get_db_cursor(commit=True) as cursor:
                cursor.execute("DELETE FROM posts")
        except psycopg2.DatabaseError as e:
            print('Error')
