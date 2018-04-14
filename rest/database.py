#!/usr/bin/python2.7
# -*- coding: utf-8 -*-

import psycopg2
from configparser import ConfigParser
import datetime
import time


class database(object):
	def __init__(self):
		params = self.config()
		self.connection = psycopg2.connect(**params)
		self.cursor = self.connection.cursor()

	def config(self, filename='database.ini', section='postgresql'):
		parser = ConfigParser()
		parser.read(filename)
		db = {}
		if parser.has_section(section):
			params = parser.items(section)
			for param in params:
				db[param[0]] = param[1]
		else:
			raise Exception('Section {0} not found in the {1} file'.format(section, filename))

		return db

	def select_all_figurine(self):
		self.cursor.execute("SELECT * FROM figurine")
		figurine = []
		row = self.cursor.fetchone()
		if row is None:
			return {'error': 'Database empty'}
		while row is not None:
			figurine.append({'id':row[0], 'name':row[1], 'points':row[2]})
			row = self.cursor.fetchone()
		return figurine

	def select_figurine(self, id):
		sql = "SELECT * FROM figurine WHERE id = %s"
		self.cursor.execute(sql, (id,))
		row = self.cursor.fetchone()
		if row is None:
			return {'error': 'Figurine does not exist'}
		figurine = {'id':row[0], 'name':row[1], 'points':row[2]}
		return figurine

	def insert_figurine(self, id, name, points):
		if self.select_figurine(id) is not None:
			self.delete_figurine(id)
			
		sql = "INSERT INTO figurine VALUES(%s, %s, %s);"
		self.cursor.execute(sql, (id, name, points))
		self.connection.commit()
			
		
	def delete_figurine(self, id):
		sql = "DELETE FROM figurine WHERE id = %s"
		self.cursor.execute(sql, (id,))
		self.connection.commit()

	def close(self):
		self.cursor.close()
		self.connection.close()