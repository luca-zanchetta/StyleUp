import mysql.connector

print('\n[INFO] Resetting data...\n')

# Creation of the database
mydb = mysql.connector.connect(
    host='localhost',
    user='root',
    password='mysql'
)
cursor = mydb.cursor()
try:
    cursor.execute('DROP DATABASE IF EXISTS styleup;')
    cursor.execute('CREATE DATABASE IF NOT EXISTS styleup;')
    print('[INFO] Database \'styleup\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the database \'styleup\': '+err)
mydb.close()


# Creation of the tables, after having created the database
mydb = mysql.connector.connect(
    host='localhost',
    user='root',
    password='mysql',
    database='styleup'
)
mycursor = mydb.cursor()


# Person table
person_drop = 'DROP TABLE IF EXISTS Person;'
person_table = '''
CREATE TABLE IF NOT EXISTS Person (
    username VARCHAR(100) PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);
'''
try:
    mycursor.execute(person_drop)
    mycursor.execute(person_table)
    print('[INFO] Table \'Person\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the table \'Person\': '+err)


# Shirt table
shirt_drop = 'DROP TABLE IF EXISTS Shirt;'
shirt_table = '''
CREATE TABLE IF NOT EXISTS Shirt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    shirt LONGBLOB NOT NULL
);
'''
try:
    mycursor.execute(shirt_drop)
    mycursor.execute(shirt_table)
    print('[INFO] Table \'Shirt\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the table \'Shirt\': '+err)


# Notification table
notification_drop = 'DROP TABLE IF EXISTS Notification;'
notification_table = '''
CREATE TABLE IF NOT EXISTS Notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    text VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    FOREIGN KEY (username) 
        REFERENCES Person (username)
        ON DELETE CASCADE,
    CONSTRAINT type_constraint 
        CHECK (type='like' OR type='friendship_request')
);
'''
try:
    mycursor.execute(notification_drop)
    mycursor.execute(notification_table)
    print('[INFO] Table \'Notification\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the table \'Notification\': '+err)


# Friend_of table
friend_of_drop = 'DROP TABLE IF EXISTS Friend_of;'
friend_of_table = '''
CREATE TABLE IF NOT EXISTS Friend_of (
    person1 VARCHAR(100) NOT NULL,
    person2 VARCHAR(100) NOT NULL,
    pending BOOLEAN NOT NULL,
    CONSTRAINT PK_Friend_of 
        PRIMARY KEY (person1, person2),
    FOREIGN KEY (person1) 
        REFERENCES Person (username)
        ON DELETE CASCADE,
    FOREIGN KEY (person2) 
        REFERENCES Person (username)
        ON DELETE CASCADE,
    CONSTRAINT person_constraint 
        CHECK (person1 <> person2)
);
'''
try:
    mycursor.execute(friend_of_drop)
    mycursor.execute(friend_of_table)
    print('[INFO] Table \'Friend_of\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the table \'Friend_of\': '+err)


# Post table
post_drop = 'DROP TABLE IF EXISTS Post;'
post_table = '''
CREATE TABLE IF NOT EXISTS Post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    shirt INT NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (username) 
        REFERENCES Person (username)
        ON DELETE CASCADE,
    FOREIGN KEY (shirt) 
        REFERENCES Shirt (id)
        ON DELETE CASCADE
);
'''
try:
    mycursor.execute(post_drop)
    mycursor.execute(post_table)
    print('[INFO] Table \'Post\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the table \'Post\': '+err)


# Likes table
likes_drop = 'DROP TABLE IF EXISTS Likes;'
likes_table = '''
CREATE TABLE IF NOT EXISTS Likes (
    person VARCHAR(100) NOT NULL,
    post INT NOT NULL,
    CONSTRAINT PK_likes 
        PRIMARY KEY (person, post),
    FOREIGN KEY (person) 
        REFERENCES Person(username)
        ON DELETE CASCADE,
    FOREIGN KEY (post) 
        REFERENCES Post(id)
        ON DELETE CASCADE
);
'''
try:
    mycursor.execute(likes_drop)
    mycursor.execute(likes_table)
    print('[INFO] Table \'Likes\' successfully created.')
except Exception as err:
    print('[ERROR] Something went wrong during the creation of the table \'Likes\': '+err)
    
print('\n[INFO] The system is ready!\n')