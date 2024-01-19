import mysql.connector
from flask import Flask, jsonify, request
from flask_cors import CORS
from datetime import datetime
from utilities import get_timestamp

app = Flask(__name__)
CORS(app)

# Middleware per gestire le richieste preflight OPTIONS
@app.before_request
def before_request():
    if request.method == "OPTIONS":
        headers = {
            "Access-Control-Allow-Origin": "*",
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
    data = request.get_json()
    username_from = data['username_from']
    username_to = data['username_to']

    # Register Notification
    query_notification = 'INSERT INTO Notification (type, text, username_from, username_to) VALUES (%s, %s, %s, %s);'
    values_notification = ('friendship_request', f'{username_from} wants to be your friend!', username_from, username_to,)

    try:
        curr.execute(query_notification, values_notification)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while sending the friendship request: '+str(err))
        return jsonify({'message':'ERROR: Send friendship request operation was not successfully performed.', 'status':500})
    

    # Register pending state in friend_of
    query_friend_of = 'INSERT INTO Friend_of (person1, person2, pending) VALUES (%s, %s, %s);'
    values_friend_of = (username_from, username_to, True)
    
    try:
        curr.execute(query_friend_of, values_friend_of)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while sending the friendship request: '+str(err))
        return jsonify({'message':'ERROR: Send friendship request operation was not successfully performed.', 'status':500})
    

    # TODO Send notification to user


    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/acceptFriendshipRequest', methods=['POST'])
def accept_friendship_request():
    data = request.get_json()
    notification_id = data['id']
    username_from = None
    username_to = None


    # Retrieve usernames from notification
    query_notification = 'SELECT username_from, username_to FROM Notification WHERE id = %s;'
    values_notification = (notification_id,)

    curr.execute(query_notification, values_notification)
    result = curr.fetchone()
    if result is not None:
        username_from = result[0]
        username_to = result[1]
    

    # Update pending status in Friend_of
    query_friend_of = 'UPDATE Friend_of SET pending = %s WHERE (person1 = %s AND person2 = %s) OR (person1 = %s AND person2 = %s);'
    values_friend_of = (False, username_from, username_to, username_to, username_from,)
    try:
        curr.execute(query_friend_of, values_friend_of)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while accepting the friendship request: '+str(err))
        return jsonify({'message':'ERROR: Accept friendship request operation was not successfully performed.', 'status':500})

    return jsonify({"message":f"{username_from} and {username_to} are now friends!", "status":200})


@app.route('/refuseFriendshipRequest', methods=['POST'])
def refuse_friendship_request():
    data = request.get_json()
    notification_id = data['id']
    username_from = None
    username_to = None


    # Retrieve usernames from notification
    query_notification = 'SELECT username_from, username_to FROM Notification WHERE id = %s;'
    values_notification = (notification_id,)

    curr.execute(query_notification, values_notification)
    result = curr.fetchone()
    if result is not None:
        username_from = result[0]
        username_to = result[1]
    

    # Delete corresponding row in Friend_of
    query_friend_of = 'DELETE FROM Friend_of WHERE (person1 = %s AND person2 = %s) OR (person1 = %s AND person2 = %s);'
    values_friend_of = (username_from, username_to, username_to, username_from,)
    try:
        curr.execute(query_friend_of, values_friend_of)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while refusing the friendship request: '+str(err))
        return jsonify({'message':'ERROR: Refuse friendship request operation was not successfully performed.', 'status':500})

    return jsonify({"message":"Friendship request has been successfully refused.", "status":200})


@app.route('/removeFriend', methods=['POST'])
def remove_friend():
    data = request.get_json()
    username_from = data['username_from']
    username_to = data['username_to']

    # Remove friendship
    query_friend_of = 'DELETE FROM Friend_of WHERE (person1 = %s AND person2 = %s) OR (person1 = %s AND person2 = %s);'
    values_friend_of = (username_from, username_to, username_to, username_from,)
    try:
        curr.execute(query_friend_of, values_friend_of)
        mydb.commit()
    except Exception as err:
        print('[ERROR] There was an error while removing the friendship: '+str(err))
        return jsonify({'message':'ERROR: Remove friendship operation was not successfully performed.', 'status':500})

    return jsonify({"message":f"{username_from} and {username_to} are no longer friends.", "status":200})


@app.route('/getFriendsPosts', methods=['GET'])
def get_friends_posts():
    username = request.args.get('username')
    friends = []
    posts = []

    # Get friends (bilateral relation!)
    query_friend_of = 'SELECT person1 FROM Friend_of WHERE person2 = %s AND pending = %s;'
    values_friend_of = (username, False,)
    curr.execute(query_friend_of, values_friend_of)
    result = curr.fetchall()

    for elem in result:
        friends.append(elem[0])

    query_friend_of = 'SELECT person2 FROM Friend_of WHERE person1 = %s AND pending = %s;'
    values_friend_of = (username, False,)
    curr.execute(query_friend_of, values_friend_of)
    result = curr.fetchall()

    for elem in result:
        friends.append(elem[0])

    if len(friends) == 0:
        return jsonify({"message":"No friends found for that username.", "status":404})


    # For each friend, get posts
    for friend_username in friends:
        query = 'SELECT id, image_data, timestamp FROM Post WHERE username = %s;'
        values = (friend_username,)

        curr.execute(query, values)
        result = curr.fetchall()

        for elem in result:
            posts.append(elem)
        
    if len(posts) == 0:
        return jsonify({"message":"No posts found.", "status":404})
        

    # Order the posts by temporal order
    sorted_posts = sorted(posts, key=get_timestamp)

    return jsonify({"posts":sorted_posts, "status":200})


@app.route('/getPostsByUsername', methods=['GET'])
def get_posts_by_username():
    username = request.args.get('username')
    posts = []

    # Get posts
    query = 'SELECT id, image_data, timestamp FROM Post WHERE username = %s;'
    values = (username,)

    curr.execute(query, values)
    result = curr.fetchall()

    for elem in result:
        posts.append(elem)

    if len(posts) == 0:
        return jsonify({"message":"No posts found with that username." , "status":404})
    
    # Sort posts by date (it is sufficient to reverse the order of the list)
    posts.reverse()

    return jsonify({"posts":posts, "status":200})


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