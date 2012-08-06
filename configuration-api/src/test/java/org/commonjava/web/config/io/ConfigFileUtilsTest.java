package org.commonjava.web.config.io;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ConfigFileUtilsTest
{

    @Test
    public void readConfigFileWithConfDGlobIncludes()
        throws IOException
    {
        final File dir = getResourcesDir();
        final InputStream stream = ConfigFileUtils.readFileWithIncludes( new File( dir, "main.conf" ) );
        final String config = IOUtils.toString( stream );

        assertThat( config.contains( "some.config = foo-bar" ), equalTo( true ) );
        assertThat( config.contains( "foo = bar" ), equalTo( true ) );
        assertThat( config.contains( "bar = baz" ), equalTo( true ) );
    }

    @Test
    public void matchNonGlobWithAbsolutePath()
        throws IOException
    {
        final File dir = getResourcesDir();
        final File[] matching =
            ConfigFileUtils.findMatching( new File( dir, "dir1/dir2a" ), new File( dir, "one.txt" ).getAbsolutePath() );

        assertThat( matching, notNullValue() );
        assertThat( matching.length, equalTo( 1 ) );

        final List<File> matchingList = Arrays.asList( matching );
        assertThat( matchingList.contains( new File( dir, "one.txt" ) ), equalTo( true ) );
    }

    @Test
    public void matchNonGlobWithRelativePath()
        throws IOException
    {
        final File dir = getResourcesDir();
        final File[] matching = ConfigFileUtils.findMatching( dir, "one.txt" );

        assertThat( matching, notNullValue() );
        assertThat( matching.length, equalTo( 1 ) );

        final List<File> matchingList = Arrays.asList( matching );
        assertThat( matchingList.contains( new File( dir, "one.txt" ) ), equalTo( true ) );
    }

    @Test
    public void globWithCharWildcard()
        throws IOException
    {
        final File dir = getResourcesDir();
        final File[] matching = ConfigFileUtils.findMatching( dir, "one?.txt" );

        assertThat( matching, notNullValue() );
        assertThat( matching.length, equalTo( 2 ) );

        final List<File> matchingList = Arrays.asList( matching );
        assertThat( matchingList.contains( new File( dir, "one1.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "one2.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "one.txt" ) ), equalTo( false ) );
    }

    @Test
    public void globWithNonDirWildcard()
        throws IOException
    {
        final File dir = getResourcesDir();
        final File[] matching = ConfigFileUtils.findMatching( dir, "one*.txt" );

        assertThat( matching, notNullValue() );
        assertThat( matching.length, equalTo( 3 ) );

        final List<File> matchingList = Arrays.asList( matching );
        assertThat( matchingList.contains( new File( dir, "one1.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "one2.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "one.txt" ) ), equalTo( true ) );
    }

    @Test
    public void globWithNonDirWildcardToTargetSpecificDirs()
        throws IOException
    {
        final File dir = getResourcesDir();
        final File[] matching = ConfigFileUtils.findMatching( dir, "dir1/*/dir3a/three.txt" );

        assertThat( matching, notNullValue() );
        assertThat( matching.length, equalTo( 2 ) );

        final List<File> matchingList = Arrays.asList( matching );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2a/dir3a/three.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2a/dir3b/three.txt" ) ), equalTo( false ) );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2b/dir3a/three.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2b/dir3b/three.txt" ) ), equalTo( false ) );
    }

    @Test
    public void globWithDirWildcard()
        throws IOException
    {
        final File dir = getResourcesDir();
        final File[] matching = ConfigFileUtils.findMatching( dir, "dir1/**/three.txt" );

        assertThat( matching, notNullValue() );
        assertThat( matching.length, equalTo( 4 ) );

        final List<File> matchingList = Arrays.asList( matching );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2a/dir3a/three.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2a/dir3b/three.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2b/dir3a/three.txt" ) ), equalTo( true ) );
        assertThat( matchingList.contains( new File( dir, "dir1/dir2b/dir3b/three.txt" ) ), equalTo( true ) );
    }

    private File getResourcesDir()
    {
        final URL resource = Thread.currentThread()
                                   .getContextClassLoader()
                                   .getResource( "marker" );
        assertThat( resource, notNullValue() );

        return new File( resource.getPath() ).getParentFile();
    }

}
