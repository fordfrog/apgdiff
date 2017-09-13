# Another PostgreSQL Diff Tool (aka apgdiff)

Ahother PostgreSQL Diff Tool is free PostgreSQL diff tool that is useful for
comparison/diffing of database schemas. You can find more information at its
website at http://apgdiff.startnet.biz/

## Changelog

### 2012-09-21: Version 2.4

#### New Features
* Added support for ALTER SEQUENCE OWNED BY (patch by Mikhail Petrov).
* Added support for CREATE TRIGGER ... UPDATE OF column.
* Added switch --ignore-slony-triggers which causes that Slony triggers
  _slony_logtrigger and _slony_denyaccess are completely ignored during parsing
  and diffing.

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
