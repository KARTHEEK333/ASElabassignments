
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var bodyParser = require("body-parser");
var express = require('express');
var cors = require('cors');
var app = express();
var url = 'mongodb://karthik3333:karthik3333@ds147073.mlab.com:47073/aselab3';

MongoClient.connect(url, function (err, database) {
    assert.equal(null, err);
    console.log("connected to server");
    database.close()
})

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.post('/register', function (req, res) {
    console.log("Inside MongoClient Register");
    MongoClient.connect(url, function(err, client) {
        if (err) {
            res.write("Failed, Error while connecting to Database");
            res.end();
        }

        var database = client.db('aselab3');

        // Check if user already exist.
        database.collection('karthik333').findOne({"email": req.body.email}, function(err, result) {
            if (err) throw err;
            if(result != null){
                console.log(result);
                res.write("Email Already exist");
                res.end();
            }else{
                database.collection('karthik333').insertOne( req.body, function(err, result) {
                    if (err) {
                        res.write("Registration Failed, Error While Registering");
                        res.end();
                    }
                    console.log("Inserted a document into the karthik333 collection.");
                    res.write("Registration Successful for the email :"+req.body.email+". Please Login to Continue.");
                    res.end();
                });
            }
        });
    });
});


// Login
app.post('/login', function (req, res) {
    console.log("Inside MongoClient Register");
    MongoClient.connect(url, function(err, client) {
        if (err) {
            res.write("Failed, Error while connecting to Database");
            res.end();
        }

        var database = client.db('aselab3');
        console.log("req.body -->"+req.body.email);
        // Check if user exist or not.
        database.collection('karthik333').findOne({"email": req.body.email}, function(err, data) {
            if (err) throw err;
            if(data == null){
                res.write("Invalid User:"+req.body.email+". No such user exist, Please Register to continue.");
                res.end();
            }else{
                // Check if user exist or not.
                database.collection('karthik333').findOne({"email": req.body.email,"password":req.body.password}
                    ,function(err, data) {
                        if (err) throw err;
                        if(data == null){
                            res.write("Invalid Password for :"+req.body.email+". Please try with a valid one");
                            res.end();
                        }else{
                            res.write("Successful Login");
                            res.end();
                        }
                    });
            }
        });
    });
});


// Update Profile
app.post('/fetchInfo', function (req, res) {
    console.log("Inside MongoClient Register");
    MongoClient.connect(url, function(err, client) {
        if (err) {
            res.write("Failed, Error while connecting to Database");
            res.end();
        }

        var database = client.db('aselab3');
        console.log("req.body -->"+req.body.email);
        // Check if user exist or not.
        database.collection('karthik333').findOne({"email": req.body.email}, function(err, data) {
            if (err) throw err;
            if(data == null){
                res.write("Invalid User:"+req.body.email+". No such user exist, Please Register to continue.");
                res.end();
            }else{
                // Check if user exist or not.
                database.collection('karthik333').findOne({"email": req.body.email,"password":req.body.password}
                    ,function(err, data) {
                        if (err) throw err;
                        if(data == null){
                            res.write("Invalid Password for :"+req.body.email+". Please try with a valid one");
                            res.end();
                        }else{
                            console.log(data);
                            res.write("Successful Login");
                            res.json(data);
                            res.end();
                        }
                    });
            }
        });
    });
});

// Login
app.post('/update', function (req, res) {
    MongoClient.connect(url, function(err, client) {
        if (err) {
            res.write("Failed, Error while connecting to Database");
            res.end();
        }

        var database = client.db('aselab3');
        var idPk = '';
        console.log("req.body -->"+req.body.email);
        database.collection('karthik333').findOne({"email": req.body.email}, function(err, data) {
            if(data != null){
                idPk = data._id;
            }
        });
        // Check if user exist or not.
        database.collection('karthik333').updateOne({_id:idPk},{
            $set: { _id: idPk,
                firstName: req.body.firstName,
                lastName: req.body.lastName,
                password: req.body.password}}, function(err, data) {
            if (err) throw err;
            res.write("Profile updated Successfully");
            res.end();
        });
    });
});

var server = app.listen(8081,function () {
    var host = server.address().address
    var port = server.address().port
    console.log("Example app listening at http://%s:%s", host, port)
});