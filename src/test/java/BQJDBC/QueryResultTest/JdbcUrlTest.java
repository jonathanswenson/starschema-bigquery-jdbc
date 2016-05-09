package BQJDBC.QueryResultTest;

import junit.framework.Assert;
import net.starschema.clouddb.jdbc.BQConnection;
import net.starschema.clouddb.jdbc.BQStatement;
import net.starschema.clouddb.jdbc.BQSupportFuncts;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by steven on 10/21/15.
 */
public class JdbcUrlTest {

    private BQConnection bq;
    private String URL;
    private Properties properties;

    @Before
    public void setup() throws SQLException, IOException {
        properties = getProperties("/installedaccount.properties");
        URL = getUrl("/installedaccount.properties", null);
        this.bq = new BQConnection(URL, new Properties());
    }

    @Test
    public void urlWithDefaultDatasetShouldWork() throws SQLException {
        Assert.assertEquals(properties.getProperty("dataset"), bq.getDataSet());
    }

    @Test
    public void canRunQueryWithDefaultDataset() throws SQLException {
        BQStatement stmt = new BQStatement(properties.getProperty("projectid"), bq);

        // This should not blow up with a "No dataset specified" exception
        stmt.executeQuery("SELECT * FROM orders limit 1");
    }

    @Test
    public void canConnectWithPasswordProtectedP12File() throws SQLException, IOException {
        String url = getUrl("/protectedaccount.properties", null);
        BQConnection bqConn = new BQConnection(url, new Properties());

        BQStatement stmt = new BQStatement(properties.getProperty("projectid"), bqConn);
        stmt.executeQuery("SELECT * FROM orders limit 1");
    }

    @Test
    public void gettingUrlComponentsWorks() throws IOException {
        String url = getUrl("/protectedaccount.properties", null);
        Properties protectedProperties = getProperties("/protectedaccount.properties");
        Map<String, String> components = BQSupportFuncts.getUrlQueryComponents(url);

        Assert.assertEquals(protectedProperties.getProperty("user"), components.get("user"));
        Assert.assertEquals(protectedProperties.getProperty("password"), components.get("password"));
        Assert.assertEquals(protectedProperties.getProperty("path"), components.get("path"));
    }

    @Test
    public void connectionUseLegacySqlValueFromProperties() throws IOException, SQLException {
        String url = getUrl("/protectedaccount.properties", null);
        BQConnection bqConn = new BQConnection(url, new Properties());
        // default false
        Assert.assertEquals(bqConn.getUseLegacySql(), true);

        String newUrl = url + "&useLegacySql=false";
        BQConnection bqConn2 = new BQConnection(newUrl, new Properties());
        Assert.assertEquals(bqConn2.getUseLegacySql(), false);
    }

    private Properties getProperties(String pathToProp) throws IOException {
        return BQSupportFuncts
                .readFromPropFile(getClass().getResource(pathToProp).getFile());
    }

    private String getUrl(String pathToProp, String dataset) throws IOException {
        return BQSupportFuncts.constructUrlFromPropertiesFile(getProperties(pathToProp), true, dataset);
    }

}