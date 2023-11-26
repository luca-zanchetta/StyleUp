import psycopg2
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



@app.route("/")
def fetch():
    return jsonify({"message":"The server is working! :)", "status":200})



##############################################################################################################
if __name__ == "__main__":
    app.run(debug=True, host="localhost", port=5000)