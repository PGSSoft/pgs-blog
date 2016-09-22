DROP KEYSPACE IF EXISTS analyzer;

CREATE KEYSPACE analyzer
WITH replication = {
	'class' : 'SimpleStrategy',
	'replication_factor' : 1
};

USE analyzer;

CREATE TABLE search_profiles (
	id uuid,
	name varchar,
	userid bigint,
	PRIMARY KEY (id)
);

CREATE TABLE search_criteria (
	id uuid,
	search_profile_id uuid,
	search_profile_name varchar,
	key_word varchar,
	musthave_word varchar,
	excluded_word varchar,
	PRIMARY KEY (id, search_profile_id)
);

CREATE TABLE documents (
	id uuid,
	url text,
	classification varchar,
	content text,
	creation_date date,
	update_date date,
	title varchar,
	tags varchar,
	PRIMARY KEY (id)
);

CREATE INDEX url_index ON documents (url);

CREATE TABLE results (
	id uuid,
	is_training_data boolean,
	classification varchar,
	search_profile_id uuid,
	document_id uuid,
	document_url text,
	document_content text,
	document_date date,
	parent_tag uuid,
	tags varchar,
	PRIMARY KEY (id, search_profile_id, document_id)
);

CREATE TABLE tags (
  id uuid,
  parent_id uuid,
  document_id uuid,
  result_id uuid,
  is_super_tag boolean,
  is_assigned_to_supertag boolean,
  content varchar,
  PRIMARY KEY(id)
);

CREATE TABLE classification_groups (
  id uuid,
  search_profile_id uuid,
  result_id uuid,
  simple_classification varchar,
  cross_classification varchar,
  train_classification varchar,
  multilayer_classification varchar,
  document_date date,
  PRIMARY KEY(id, result_id)
);

CREATE TABLE algorithm_accuracy (
  id uuid,
  name varchar,
  accuracy int,
  search_profile_id uuid,
  PRIMARY KEY(id)
);

CREATE INDEX is_training_data_index ON results (is_training_data);

CREATE INDEX classification_index ON results (classification);

CREATE INDEX document_date_index ON results (document_date);

CREATE OR REPLACE FUNCTION date_group_and_count( state map<date, int>, type date )
CALLED ON NULL INPUT
RETURNS map<date, int>
LANGUAGE java AS '
Integer count = (Integer) state.get(type);  if (count == null) count = 1; else count++; state.put(type, count); return state; ' ;

CREATE OR REPLACE AGGREGATE group_and_count(date)
SFUNC date_group_and_count
STYPE map<date, int>
INITCOND {};