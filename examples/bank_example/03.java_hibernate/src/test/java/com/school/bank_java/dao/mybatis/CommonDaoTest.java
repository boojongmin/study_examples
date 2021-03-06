package com.school.bank_java.dao.mybatis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDaoTest {
	private static final Logger commonLogger = LoggerFactory.getLogger(CommonDaoTest.class);

	private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
//	private static final String JDBC_URL = "jdbc:h2:mem:test;MODE=MYSQL";
	private static final String JDBC_URL = "jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
	private static final String USER = "sa";
	private static final String PASSWORD = "1234";
	private static final String SCHEMA = "classpath:com/school/bank_java/dao/schema.sql";
	private static final String TRUNCATE = "classpath:com/school/bank_java/dao/truncate.sql";
	private static final String DATASET = "com/school/bank_java/dao/dataset.xml";
	public static final String MYBATIS_CONFIG = "com/school/bank_java/dao/mybatis/mybatis-test-config.xml";


//	@BeforeClass
//    public static void createSchema() throws SQLException {
//         RunScript.execute(JDBC_URL, USER, PASSWORD, SCHEMA, Charset.forName("UTF-8"), false);
//    }

	@Before
	public void importDataSet() throws Exception {
        RunScript.execute(JDBC_URL, USER, PASSWORD, SCHEMA, Charset.forName("UTF-8"), false);
//        RunScript.execute(JDBC_URL, USER, PASSWORD, TRUNCATE, Charset.forName("UTF-8"), false);
		IDataSet dataSet = readDataSet();

		cleanlyInsert(dataSet);
	}

	public IDataSet readDataSet() throws Exception {
		InputStream dataSource = this.getClass().getClassLoader().getResourceAsStream(DATASET);
//		InputStream dataSource = new FileInputStream(DATASET);
		return new FlatXmlDataSetBuilder().build(dataSource);
	}

	public void cleanlyInsert(IDataSet dataSet) throws Exception {
		IDatabaseTester databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
		databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
		databaseTester.setDataSet(dataSet);
		databaseTester.onSetup();
	}

//	@Test
//	public void findsAndReadsExistingPersonByFirstName() throws Exception {
//		PersonRepository repository = new PersonRepository(dataSource());
//		Person charlie = repository.findPersonByFirstName("Charlie");
//
//		assertThat(charlie.getFirstName(), is("Charlie"));
//		assertThat(charlie.getLastName(), is("Brown"));
//		assertThat(charlie.getAge(), is(42));
//	}
//
//	@Test
//	public void returnsNullWhenPersonCannotBeFoundByFirstName() throws Exception {
//		PersonRepository repository = new PersonRepository(dataSource());
//		Person person = repository.findPersonByFirstName("iDoNotExist");
//
//		assertThat(person, is(nullValue()));
//	}
}