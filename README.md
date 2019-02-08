# Another PostgreSQL Diff Tool (aka apgdiff)

Another PostgreSQL Diff Tool is free PostgreSQL diff tool that is useful for
comparison/diffing of database schemas. You can find more information at its
website at http://www.apgdiff.com/. If you found an issue in apgdiff, please
file it at https://github.com/fordfrog/apgdiff/issues. If you for some reason
need to contact author of this application, you can email him at
fordfrog@fordfrog.com.

This repo is mainly unmaintained. But if you found a bug and create a pull request chances are good that it will be merged.

## Changelog

### Version 2.6

#### New Features 
* PostgresSQL 10 Support
* Update dependencies to development with Java 11
* Add security barrier and another options to Views
* Add support to Create/Drop Rule
* Triggers: Rerencing,Enable,Disable
* use using column::type to cast the type of column

#### Fixes
* Fix GRANT SEQUENCE, ALTER VIEW OWNER, GRANT(cols) errors

#### Tasks Done
* Remove SourceForge repository

### Version 2.5

#### New Features
* MATERIALIZED VIEW support in PostgreSQL 9.3 (Marti Raudsepp)
* Better support for inherited tables (Daniel Watson)
* Added support for CREATE UNLOGGED TABLE (Anatoliy Basov)
* Added support for /**/ comments (yulei)
* Support of triggers for views + clause 'INSTEAD OF' (Sergej Bonich)
* Add support for GRANT and REVOKE on objects: table, view, sequence, column (serge-pouliquen-itf)
* Add support for ALTER TABLE ... OWNER TO (serge-pouliquen-itf)
* Add support for CREATE TYPE (Karol Rybak)
* Add support for CREATE EXTENSION (Átila Camurça Alves)
* Add basic support for CREATE FOREIGN TABLE (Bruno Almeida)

#### Fixes
* Added hint to use "CREATE TABLE ... CONSTRAINT name PRIMARY KEY/UNIQUE ..."
  instead of "CREATE TABLE ... PRIMARY KEY/UNIQUE ..." because apgdiff cannot
  easily support unnamed constraints.
* Fixed issue with incorrect end of expression detection because of ignored [
  and ] brackets. This caused issues for example in statements like
  "... DEFAULT ARRAY[1, 2, 3], ..." where end of expression was detected at
  first comma (and not the third one) which then resulted in parser exception.
* Fixed issue when outputting unsupported command information and the
  unsupported command string is shorter than 20 characters. (Linas Valiukas)
* Added Spanish translation. (Sebastian Ortiz)
* Fitted English help to 80 characters in width. (Dave Jarvis)
* View query changes are now correctly detected even if it has declared
  columns that didn't change. (Marti Raudsepp)
* Fixed issue with the $ sign in the object name (Anatoliy Basov)
* Added French translation. (Jeremy Passeron)
* Native for OS line endings in resulting diff (Sergej Bonich)
* Add support for new Postgres schema dump format
* Support for Postgres 10 CREATE SEQUENCE data type

### 2012-09-21: Version 2.4

#### New Features
* Added support for ALTER SEQUENCE OWNED BY (patch by Mikhail Petrov).
* Added support for CREATE TRIGGER ... UPDATE OF column.
* Added switch --ignore-slony-triggers which causes that Slony triggers
  _slony_logtrigger and _slony_denyaccess are completely ignored during parsing
  and diffing.
* Added switch --ignore-schema-creation which removes the need of CREATE SCHEMA
  declararions in the input files.

#### Fixes
* Fixed issue with comments not being added on newly created columns.
* Improved logging errors when parsing strings.
* Added support for IF NOT EXISTS (patch by Felipe Sateler).
* Fixed NPE when search_path contains quoted schema (patch by Steven Elliott).
* Fixed dropping of default values when --add-defaults is specified (patch by
  Jim Mlodgenski).
* Fixed all bugs related to incorrect parsing of end of statement, most often
  resulting in StringIndexOutOfBoundException.
* Fixed CREATE TABLE statement output when table contains no column.

### 2010-10-22: Version 2.3

#### New Features
* Added support for diffing of COMMENT ON statements.
* Added switch --list-charsets to output list of supported charsets.

#### Fixes
* Added user error messages instead of NullPointerException for cases when
  referenced database object was not found in the dump.
* Fixed bug with parsing quoted object name when both first and second part of
  the name was quoted.
* Fixed bug in parser that caused in some cases invalid match against expected
  "word".
* Fixed bug in parser that caused array data types not detected correctly (was
  caused by fix of invalid match of "word" above).
* Functions are now created after tables are create and updated, because of
  functions depending on tables and columns. Later will be implemented solution
  for cases where functions have to be created before table columns.

### 2010-10-09: Version 2.2.2

#### Fixes
* Added missing new line after ALTER VIEW ... ALTER COLUMN ... SET/DROP DEFAULT.
* Fixed parsing of quoted string values.
* Fixed detection of function body separator (did not work when there was
  another 'AS' specified on the same line after the 'AS' starting function
  body).
* If two dumps are completely same and there is more than one schema in the
  dumps, the output now does not contain 'SET search_path = ...'. In other
  words, if two dumps are completely same, no output is produced.
* Replaced 'ALTER VIEW name ALTER COLUMN ...' with 'ALTER TABLE view_name ALTER
  COLUMN ...' to make it compatible with PostgreSQL releases prior to 8.4.
* Fixed parsing of '' escapes.

#### Other
* Added support for localization of apgdiff.
* Added Czech localization.

### 2010-10-03: Version 2.2.1

#### New Features
* Commands like OWNER TO and ENABLE/DISABLE TRIGGER/RULE are now added to the
  diff output even for commands that are otherwise being parsed, like ALTER
  TABLE.

#### Fixes
* Fixed bug where default values were dropped from VIEW columns even if they
  were not modified.

### 2010-10-02: Version 2.2

#### New Features
* Statements that are not supported by apgdiff yet are now all ignored(till this
  release apgdiff had to be instructed to know what commands to ignore which was
  not good solution). This is the same behavior as in 1.* releases.
* Added command line switch <code>--output-ignored-statements</code> which can
  be used to output statements that apgdiff ignores in the dump files. This
  feature makes more clear what statements were ignored and developer has to
  handle them manually eventually.

### 2010-09-30: Version 2.1

#### New Features
* Added support for ALTER VIEW.
* Added support for ALTER TABLE view_name/sequence_name.

#### Fixes
* Fixed issue with comparison of VIEWs when columns are not specified but query
  has changed.
* Fixed parsing of quoted names at many places.
* CREATE RULE is now silently skipped.

### 2010-09-16: Version 2.0.2

#### Fixes
* CREATE DOMAIN is now silently skipped.

### 2010-09-16: Version 2.0.1

#### Fixes
* CREATE OPERATOR and ALTER LANGUAGE are now silently skipped.

### 2010-09-13: Version 2.0 Including Beta Releases

#### New Features
* SQL parser has been completely rewritten to allow safer and more flexible
  parsing of SQL statements.
* Statements not supported by apgdiff are now not silenty ignored if apgdiff is
  not told (by me in code) to ignore them.
* Added support for ALTER TABLE ... ALTER COLUMN ... SET STORAGE
  PLAIN|EXTERNAL|EXTENDED|MAIN.
* Added support for CREATE TABLE ... TABLESPACE.
* Updated parsing of CREATE TABLE ... WITH/WITHOUT OIDS.
* Added support for CREATE TRIGGER ... WHEN and for even TRUNCATE.
* Added support for CREATE SEQUENCE ... OWNED BY.
* CREATE SCHEMA is now supported for both syntaxes.
* Added support for default values on function arguments.
* Added support for parsing ALTER TABLE ... ENABLE/DISABLE TRIGGER/PARSER, but
  they are not diffed for now.

#### Fixes
* ALTER SEQUENCE and CREATE AGGREGATE are now silently skipped.
* Fixed parsing of end of function.
* Improved handling of dotted quoted names.
* Fixed quoting of SQL reserved keywords.
* Fixed parsing of function arguments.
* Triggers are now dropped before functions are dropped (bug #2991245).
* Improved diffing of CREATE TABLE ... INHERITS.

### Versions Prior to 2.0

These versions are not covered in changelog.
