# SQLAnalyzer
<b>SQLAnalyzer</b>, an Open source SQL Query analysis library. Now don't just write SQL Query, understand the behind scene actions. 

# Why SQLAnalyzer created?
Developer thinks having high processing hardware allows them to interact database with unstructured SQL Query. Well writing structured query is really a big deal.

Now Developer can optimize the SQL Query in development phase using SQLAnalyzer and SQL Queries will give their best on Production environment. SQLAnalyzer can generate Graphical Analysis report of your SQL Query right from the CODE no extra configuration required on database. 

#Features
<table>
  <tr>
    <td>✔ Analyze SQL Query</td>
    <td>✔ Analyze SQL Plans</td>
  </tr>
  <tr>
    <td>✔ Graphical Representation</td>
    <td>✔ Query Statistics</td>
  </tr>
  <tr>
    <td>✔ Browser based HTML Report</td>
    <td>✔ Lightweight</td>
  </tr>
</table>

#Database Support
<table>
	<tr>
		<td>✔ Microsoft SQL Server</td>
		<td>✔ MySQL (5.6 or above)</td>
	</tr>
	<tr>
		<td>✔ PostgreSQL</td>
	</tr>
</table>

#Source code (Step 1)
Create class and extend Database Service. [<b>MSSQLServiceImpl, MySQLServiceImpl, PostgreSQLServiceImpl</b>]
<pre>
package com.sqlanalyzer.test;

import com.sqlanalyzer.database.service.MySQLServiceImpl;

/**
 * @author vicky.thakor
 */
public class MySQLApi extends MySQLServiceImpl{

    @Override
    public String DatabaseDriver() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String DatabaseHost() {
        return "jdbc:mysql://localhost:3306/sqlanalyzer";
    }

    @Override
    public String DatabaseUsername() {
        return "root";
    }

    @Override
    public String DatabasePassword() {
        return "root";
    }
}
</pre>

#Source code (Step 2)
Generate report from SQL Query. [<b>Note</b>: Real query must be execcuted before preparing report.]
<pre>
public static void main(String[] args) throws IOException {
        List<SQLPlan> sQLPlans = new SQLAnalyzer(MySQLApi.class, null)
                .initDatabaseConnection()
                .fromQuery("select * from user_master this_ inner join message messages1_ on this_.id=messages1_.user_id inner join creditcard creditcard2_ on this_.id=creditcard2_.user_id where this_.email='vicky.thakor@javaquery.com'")
                .save("D:\\SQLAnalyzer\\MySQL", "SELECT", "_STAR")
                .generateReport();
        
        for (SQLPlan sqlPlan : sQLPlans) {
            /**
             * Customize graphical report as per your requirement.
             * Put String `htmlReport` anywhere between <html> block.
             */
            String htmlReport = sqlPlan.getHTMLReport();
            
            /* Open report saved to Physical location. */
            Desktop.getDesktop().open(new File(sqlPlan.reportFiles().get(0)));
        }
}
</pre>

#Sample Reports
<a href="https://javaquery.github.io/SQLAnalyzer/">https://javaquery.github.io/SQLAnalyzer/</a>
- <a href="http://javaquery.github.io/SQLAnalyzer/mssql">Microsoft SQL Server</a> 
- <a href="http://javaquery.github.io/SQLAnalyzer/mysql">MySQL</a>
- <a href="http://javaquery.github.io/SQLAnalyzer/postgresql">PostgreSQL</a>

#Add-ons
- <a href="https://github.com/javaquery/HibernateSQLAnalyzer">HibernateSQLAnalyzer</a> allows you to prepare analysis report directly from <a href="https://en.wikipedia.org/wiki/Hibernate_(framework)">Hibernate Criteria</a>

#Warning
SQLAnalyzer is analysis tool and should be used at development phase. It'll cost a lot on Production environment so comment/delete SQLAnalyzer code before you deploy your code on Production environment. 
