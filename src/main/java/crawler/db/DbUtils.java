package crawler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import crawler.novel.Article;
import crawler.novel.Chapter;

public class DbUtils {
	// http://miao19880124.iteye.com/blog/992576
	// private static final String URL =
	// "jdbc:postgresql://127.0.0.1:8432/yidu"; // JDBC连接URL
//	private static final String URL = "jdbc:postgresql://192.168.1.110:5432/yidu"; // JDBC连接URL
	private static final String URL = "jdbc:postgresql://192.168.99.128:5432/yidu"; // JDBC连接URL
	private static final String USR = "postgres"; // 用户名
	private static final String PWD = "534568"; // 密码

	private static Connection connection_ = null;
	
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
			
			String sql = Article.getSqlArticlePrefix() + " VALUES (";
			sql += "";
			Article a = new Article();
			Statement p = con.createStatement();
			//p.execute(a.Sql());
			Chapter c = new Chapter();
			for (int i = 1; i <= 1000; ++i) {
				a.setCategory(i % 10 + 1);
				a.setArticlename("语文第" + i +"部");
				p.execute(a.Sql());
				//c.setChaptername("第" + i +"章 xxx");
				//p.execute(c.Sql());
			}
			rs.close();
			pstmt.close();
			p.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
