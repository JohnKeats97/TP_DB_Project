from flask import request

@app.route('/api/user/<nickname>/create', methods=['POST'])
def create_user(nickname):

    return code


@app.route('/api/user/<nickname>/profile', methods=['GET', 'POST'])
def view_profile(nickname):
   
    return code


@app.route('/api/forum/create', methods=['POST'])
def create_forum():
    
    return code


@app.route('/api/forum/<slug>/details', methods=['GET'])
def view_forum_info(slug):
    
    return code


@app.route('/api/forum/<slug>/create', methods=['POST'])
def create_thread(slug):
    
    return code


@app.route('/api/forum/<slug>/threads', methods=['GET'])
def get_forum_threads(slug):
    
    return code


@app.route('/api/thread/<slug_or_id>/create', methods=['POST'])
def create_posts(slug_or_id):
    
    return code


@app.route('/api/thread/<slug_or_id>/vote', methods=['POST'])
def vote(slug_or_id):
    
    return code


@app.route('/api/thread/<slug_or_id>/details', methods=['GET', 'POST'])
def view_thread(slug_or_id):
    
    return code


@app.route('/api/thread/<slug_or_id>/posts', methods=['GET'])
def get_posts_sorted(slug_or_id):
    
    return  code


@app.route('/api/forum/<slug>/users', methods=['GET'])
def get_forum_users(slug):
    
    return code


@app.route('/api/post/<id>/details', methods=['GET', 'POST'])
def get_post_detailed(id):
    
    return code


@app.route('/api/service/status', methods=['GET'])
def status():

    return code


@app.route('/api/service/clear', methods=['POST'])
def clear():

    return code
