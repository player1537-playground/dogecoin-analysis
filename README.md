# Building

Currently, the project depends on a local version of
[jReddit](https://github.com/jReddit/jReddit) which is packaged in the
repository. In order to make this in a format usable with Maven, the
script
[install-to-project-repo](https://github.com/nikita-volkov/install-to-project-repo)
is used. Once downloaded, running the following command will set up
the local repo:

    $ pwd
    /path/to/dogecoin-analysis/
    $ python /path/to/install-to-project-repo
    ...
    <dependency>
        <groupId>com.github.jreddit</groupId>
        <artifactId>jreddit</artifactId>
        <version>1.0.3-SNAPSHOT</version>
    </dependency>

The dependency output that the script outputs should match the one in
`pom.xml`.

This process is better documented in this
[Stack Overflow answer](http://stackoverflow.com/a/7623805).
