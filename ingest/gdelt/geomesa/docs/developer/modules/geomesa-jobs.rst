.. _geomesa-jobs:

geomesa-jobs
============

Building Instructions
~~~~~~~~~~~~~~~~~~~~~

This project contains map/reduce and scalding jobs for maintaining
GeoMesa.

If you wish to build this project separately, you can with maven:

.. code:: shell

    geomesa> mvn clean install -pl geomesa-jobs

GeoMesa Input and Output Formats
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

GeoMesa provides input and output formats that can be used in Hadoop
map/reduce jobs. The input/output formats can be used directly in Scala,
or there are Java interfaces under the ``interop`` package.

The input/output formats have two versions each, for compatibility with
the 'old' Hadoop api (under the ``mapred`` package) and the 'new' Hadoop
api (under the ``mapreduce`` package).

There are sample jobs provided that can be used as templates for more
complex operations. These are:

::

    org.locationtech.geomesa.jobs.interop.mapred.FeatureCountJob
    org.locationtech.geomesa.jobs.interop.mapred.FeatureWriterJob
    org.locationtech.geomesa.jobs.interop.mapreduce.FeatureCountJob
    org.locationtech.geomesa.jobs.interop.mapreduce.FeatureWriterJob

GeoMesaInputFormat
^^^^^^^^^^^^^^^^^^

The ``GeoMesaInputFormat`` can be used to get ``SimpleFeature``\ s into
your jobs directly from GeoMesa.

Use the static ``configure`` method to set up your job. You need to
provide it with a map of connection parameters, which will be used to
retrieve the GeoTools DataStore. You also need to provide a feature type
name. Optionally, you can provide a CQL filter, which will be used to
select a subset of features in your store.

The key provided to your mapper with be a ``Text`` with the
``SimpleFeature`` ID. The value will be the ``SimpleFeature``.

GeoMesaOutputFormat
^^^^^^^^^^^^^^^^^^^

The ``GeoMesaOutputFormat`` can be used to write ``SimpleFeature``\ s
back into GeoMesa.

Use the static ``configure`` method to set up your job. You need to
provide it with a map of connection parameters, which will be used to
retrieve the GeoTools ``DataStore``. Optionally, you can also configure
the BatchWriter configuration used to write data to Accumulo.

The key you output does not matter, and will be ignored. The value
should be a ``SimpleFeature`` that you wish to write. If the
``SimpleFeatureType`` associated with the ``SimpleFeature`` does not yet
exist in GeoMesa, it will be created for you. You may write different
``SimpleFeatureType``\ s, but note that they will all share a common
catalog table.

Map/Reduce Jobs
~~~~~~~~~~~~~~~

To facilitate running jobs, you may wish to build a shaded jar that
contains all the required dependencies. Ensure that the pom references
the correct versions of hadoop, accumulo etc for your cluster, then
build the project using the 'assemble' profile

.. code:: shell

    geomesa> mvn clean install -P assemble -pl geomesa-jobs

The following instructions assume you have built a shaded jar; if not
you will need to use the 'libjars' argument to ensure the correct jars
are available on the distributed classpath.

Attribute Indexing
^^^^^^^^^^^^^^^^^^

GeoMesa provides indexing on attributes to improve certain queries. You
can indicate attributes that should be indexed when you create your
schema (simple feature type). If you decide later on that you would like
to index additional attributes, you can use the attribute indexing job.
You only need to run this job once.

The job can be invoked through yarn as follows (jar version may vary
slightly):

.. code:: shell

    geomesa> yarn jar geomesa-jobs/target/geomesa-jobs-1.0.0-shaded.jar \
        org.locationtech.geomesa.jobs.index.AttributeIndexJob \
        --geomesa.input.instanceId <instance> \
        --geomesa.input.zookeepers <zookeepers> \
        --geomesa.input.user <user> \
        --geomesa.input.password <pwd> \
        --geomesa.input.tableName <catalog-table> \
        --geomesa.input.feature <feature> \
        --geomesa.index.coverage <full|join> \ # optional attribute
        --geomesa.index.attributes <attributes to index - space separated>

(Note that if you did not build with the 'assemble' profile, you will
also need to include an extensive -libjars argument with all dependent
jars)

Updating Existing Data to the Latest Index Format
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

GeoMesa is constantly improving our indexing. We strive to maintain
backwards comaptiblity, but old data can't always take advantage of the
improvements we make. However, old data can be updated through the
SchemaCopyJob. This will copy it to a new table (or feature name),
rewriting all the data using the latest codebase. Once the data is
updated, you can drop the old tables and rename the new tables back to
the original names.

The job can be invoked through yarn as follows (jar version may vary
slightly):

.. code:: shell

    yarn jar geomesa-jobs/target/geomesa-jobs-1.0.0-shaded.jar \
        org.locationtech.geomesa.jobs.index.SchemaCopyJob \
        --geomesa.input.instanceId <instance> \
        --geomesa.output.instanceId <instance> \
        --geomesa.input.zookeepers <zookeepers> \
        --geomesa.output.zookeepers <zookeepers> \
        --geomesa.input.user <user> \
        --geomesa.output.user <user> \
        --geomesa.input.password <pwd> \
        --geomesa.output.password <pwd> \
        --geomesa.input.tableName <catalog-table> \
        --geomesa.output.tableName <new-catalog-table> \
        --geomesa.input.feature <feature> \
        --geomesa.output.feature <feature> \
        --geomesa.input.cql <options cql filter for input features>

(Note that if you did not build with the 'assemble' profile, you will
also need to include an extensive -libjars argument with all dependent
jars)
