import mysql.connector
from flask import Flask, jsonify, request
from flask_cors import CORS
from datetime import datetime

app = Flask(__name__)
CORS(app)

# Middleware per gestire le richieste preflight OPTIONS
@app.before_request
def before_request():
    if request.method == "OPTIONS":
        headers = {
            "Access-Control-Allow-Origin": "http://localhost:3000",
            "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE",
            "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept",
            "Access-Control-Allow-Credentials": "true",
        }
        return ("", 200, headers)
    

# DB setup
mydb = mysql.connector.connect(
    host='localhost',
    user='root',
    password='mysql',
    database='styleup'
)
curr = mydb.cursor()


@app.route('/')
def fetch():
    return jsonify({"message":"The server is working! :)", "status":200})


############################ ACCOUNT MANAGEMENT APIs #####################################
@app.route('/register', methods=['POST'])
def create_account():
    data = request.get_json()
    username = data['username']
    email = data['email']
    password = data['password']
    
    # The check about whether the username is correct or not is entirely done by the DBMS
    query = 'INSERT INTO Person (username, email, password) VALUES (%s, %s, %s);'
    values = (username, email, password,)
    try:
        curr.execute(query, values)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while inserting the new person: '+str(err))
        return jsonify({'message':'ERROR: The username is not valid.', 'status':400})
    
    return jsonify({"message":"You have registered successfully!", "status":200})


@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data['username']
    password = data['password']
    
    query = 'SELECT username, password FROM Person WHERE username = %s;'
    values = (username,)
    curr.execute(query, values)
    result = curr.fetchall()
    
    for elem in result:
        if elem[1] == password:
            return jsonify({'message':'Login successfully performed!', 'status':200})
        elif elem[2] != password:
            return jsonify({'message':'ERROR: Wrong username and/or password.', 'status':400})
    
    # If we get here, the username is wrong
    return jsonify({'message':'ERROR: Wrong username and/or password.', 'status':400})


@app.route('/updateAccount', methods=['POST'])
def update_account():
    data = request.get_json()
    old_username = data['old_username']
    username = data['username']
    email = data['email']
    password = data['password']
    
    query = 'UPDATE Person SET username = %s, email = %s, password = %s WHERE username = %s;'
    values = (username, email, password, old_username)
    try:
        curr.execute(query, values)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while modifying the account: '+str(err))
        return jsonify({'message':'ERROR: Modify operation was not successfully performed.', 'status':500})
        
    return jsonify({"message":"Your data was modified successfully!", "status":200})


@app.route('/deleteAccount', methods=['POST'])
def delete_account():
    data = request.get_json()
    username = data['username']
    
    query = 'DELETE FROM Person WHERE username = %s;'
    values = (username,)
    try:
        curr.execute(query, values)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while deleting the account: '+str(err))
        return jsonify({'message':'ERROR: Delete operation was not successfully performed.', 'status':500})
    
    return jsonify({'message':'Your account was successfully deleted!', 'status':200})


########################## PICTURES & POSTS MANAGEMENT APIs ##############################
@app.route('/createPost', methods=['POST'])
def create_post():
    data = request.get_json()
    username = data['username']
    shirt_id = data['shirt']
    image_data = data['image']
    
    timestamp = datetime.now()
    timestamp = timestamp.strftime('%Y-%m-%d %H:%M:%S')
    
    query = 'INSERT INTO Post (username, shirt, image_data, timestamp) VALUES (%s, %s, %s, %s);'
    values = (username, shirt_id, image_data, timestamp)
    
    try:
        curr.execute(query, values)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while creating the post: '+str(err))
        return jsonify({'message':'ERROR: Create post operation was not successfully performed.', 'status':500})
    
    return jsonify({"message":"The post was successfully created!", "status":200})


@app.route('/deletePost', methods=['POST'])
def delete_post():
    data = request.get_json()
    post_id = data['post']
    
    query = 'DELETE FROM Post WHERE id = %s;'
    values = (post_id,)
    
    try:
        curr.execute(query, values)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while deleting the post: '+str(err))
        return jsonify({'message':'ERROR: Delete post operation was not successfully performed.', 'status':500})
    
    return jsonify({"message":"The post was successfully deleted!", "status":200})

# Maybe there should be another api for the shirt try-on, but I don't know

############################## FRIENDS MANAGEMENT APIs ################################
@app.route('/getUserByUsername', methods=['GET'])
def get_user_by_username():
    username = request.args.get('username')
    
    query = f"SELECT * from Person WHERE username REGEXP '{username}.*';"
    curr.execute(query)
    result = curr.fetchall()
    
    for user in result:
        return jsonify({'user':user, 'status':200})
    
    return jsonify({'user':[], 'status':404})


@app.route('/getNotifications', methods=['GET'])
def get_notifications():
    username = request.args.get('username')
    notifications = []
    
    query = 'SELECT * FROM Notification WHERE username = %s;'
    values = (username,)
    
    curr.execute(query, values)
    result = curr.fetchall()
    
    for elem in result:
        notifications.append(elem)
    
    return jsonify({'notifications':notifications, "status":200})


@app.route('/sendFriendshipRequest', methods=['POST'])
def send_friendship_request():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/acceptFriendshipRequest', methods=['POST'])     # ok
def accept_friendship_request():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/refuseFriendshipRequest', methods=['POST'])    # ok
def refuse_friendship_request():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/removeFriend', methods=['POST'])   # ok
def remove_friend():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/getFriendsPosts', methods=['GET']) # ok
def get_friends_posts():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/getPostsByUsername', methods=['GET'])  # ok
def get_posts_by_username():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


########################### INTERACTIONS MANAGEMENT APIs #################################
@app.route('/likePost', methods=['POST'])
def like_post():
    data = request.get_json()
    username = data['username']
    post_id = data['post']
    
    query = 'INSERT INTO Likes (person, post) VALUES (%s, %s);'
    values = (username, post_id)
    
    try:
        curr.execute(query, values)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while inserting the like: '+str(err))
        return jsonify({'message':'ERROR: Like operation was not successfully performed.', 'status':500})
    
    return jsonify({"message":"You liked the post successfully!", "status":200})


@app.route('/getLikesByPost', methods=['GET'])
def get_likes_by_post():
    post_id = request.args.get('post')
    usernames = []
    
    query = 'SELECT person FROM Likes WHERE post = %s;'
    values = (post_id,)
    
    curr.execute(query, values)
    result = curr.fetchall()
    
    for elem in result:
        usernames.append(elem[0])
    
    return jsonify({"likes":usernames, "n_likes":len(usernames), "status":200})



##############################################################################################################
if __name__ == "__main__":
    app.run(debug=True, host="localhost", port=5000)