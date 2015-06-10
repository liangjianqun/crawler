package crawler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
	// http://miao19880124.iteye.com/blog/992576
	// private static final String URL =
	// "jdbc:postgresql://127.0.0.1:8432/yidu"; // JDBC连接URL
	private static final String URL = "jdbc:postgresql://192.168.1.110:5432/yidu"; // JDBC连接URL
	private static final String USR = "postgres"; // 用户名
	private static final String PWD = "534568"; // 密码

	private static Connection connection_ = null;
	private static String sql_article_prefix_ = "INSERTO t_article(articleno,articlename,pinyin,pinyinheadchar,initial,keywords,"+
			"authorid,author,category,subcategory,intro,lastchapterno,lastchapter,chapters,size,"+
			"fullflag,imgflag,agent,firstflag,permission,authorflag,postdate,lastupdate,"+
			"dayvisit,weekvisit,monthvisit,allvisit,dayvote,weekvote,monthvote,allvote,deleteflag,"+
			"publicflag,createusernoeger,createtime,modifyusernoeger,modifytime) VALUES ";
	
	static {
		try {
			// 加载驱动
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("驱动加载出错！");
			e.printStackTrace();
		}
	}

	public static Connection GetConnection() {
		if (connection_ != null) {
			return connection_;
		}

		try {
			connection_ = DriverManager.getConnection(URL, USR, PWD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection_;
	}

	public static void main(String[] args) {
		try {
			Connection con = DbUtils.GetConnection();
			PreparedStatement pstmt = con.prepareStatement("select * from t_user");

			//pstmt.executeUpdate();
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println("total:" + rs.getString("loginid"));
			}
			
			String sql = sql_article_prefix_ + " VALUES (";//"INSERT INTO t_user(loginid, password,type,deleteflag,activedflag) VALUES ('xxx', 'xx', 30 ,false,true);";
			sql += "";
			
			Statement p = con.createStatement();
			p.execute(sql);
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}