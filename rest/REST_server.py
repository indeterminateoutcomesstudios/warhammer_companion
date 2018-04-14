#!/usr/bin/python2.7
# -*- coding: utf-8 -*-

from flask import *
from database import *
from configparser import ConfigParser
import requests
from rules import *

app = Flask(__name__)

@app.route('/figurine_list', methods=['GET'])
def get_all_figurines():
	return jsonify(db.select_all_figurine())

@app.route("/figurine/<int:id>", methods=['GET', 'POST', 'DELETE'])
def info(id):
    if request.method == 'GET':
        return jsonify(db.select_figurine(id))

    if request.method == 'POST':
        name = request.form['name']
        points = request.form['points']
        return jsonify(db.insert_figurine(id, name, points))

    if request.method == 'DELETE':
        return jsonify(db.delete_figurine(id))

    return '',400

if __name__ == '__main__':
    db = database()
    parser = ConfigParser()
    parser.read('rest_server.ini')
    if parser.has_section('rest_server'):
    	params = parser.items('rest_server')
    	ip = params[0]
    else:
    	raise Exception('Section {0} not found in the {1} file'.format(section, filename))
    app.run(debug=True,host=ip[1])
