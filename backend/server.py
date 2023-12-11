import mysql.connector
from flask import Flask, jsonify, request
from flask_cors import CORS

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

@app.route('/')
def fetch():
    return jsonify({"message":"The server is working! :)", "status":200})


############################ ACCOUNT MANAGEMENT APIs #####################################
@app.route('/register', methods=['POST'])
def create_account():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/login', methods=['POST'])
def login():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/updateAccount', methods=['POST'])
def update_account():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/deleteAccount', methods=['POST'])
def delete_account():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


########################## PICTURES & POSTS MANAGEMENT APIs ##############################
@app.route('/createPost', methods=['POST'])
def create_post():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/deletePost', methods=['POST'])
def delete_post():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


# Maybe there should be another api for the shirt try-on, but I don't know

############################## FRIENDS MANAGEMENT APIs ################################
@app.route('/getUserByUsername', methods=['GET'])
def get_user_by_username():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/getNotifications', methods=['GET'])
def get_notifications():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/sendFriendshipRequest', methods=['POST'])
def send_friendship_request():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/acceptFriendshipRequest', methods=['POST'])
def accept_friendship_request():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/refuseFriendshipRequest', methods=['POST'])
def refuse_friendship_request():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/removeFriend', methods=['POST'])
def remove_friend():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/getFriendsPosts', methods=['GET'])
def get_friends_posts():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/getPostsByUsername', methods=['GET'])
def get_posts_by_username():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


########################### INTERACTIONS MANAGEMENT APIs #################################
@app.route('/likePost', methods=['POST'])
def like_post():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})


@app.route('/getLikesByPost', methods=['GET'])
def get_likes_by_post():
    return jsonify({"message":"WORK IN PROGRESS...", "status":202})



##############################################################################################################
if __name__ == "__main__":
    app.run(debug=True, host="localhost", port=5000)