package io.koschicken.utils;

import java.sql.*;

public class InitDatabase {
    private static final int NEW_VERSION = 7;

    public void initDB() {
        int version = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:root.db");
             PreparedStatement preparedStatement = conn.prepareStatement("select version from version")) {
            Class.forName("org.sqlite.JDBC");
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                version = set.getInt("version");
            }
            updateData(version, conn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            //存在新建db文件的情况，sql查询可能出错
        }
    }

    public void updateData(int version, Connection conn) {
        try (Statement statement = conn.createStatement()) {
            switch (version) {
                case -1://没有数据库版本标识，重新建立数据库
                    statement.executeUpdate("create table Scores(QQ integer primary key, nickname varchar(50), iSign boolean default false, score integer(8) default 0, groupCode text)");
                    statement.executeUpdate("create table 'version'('version' integer)");
                    statement.executeUpdate("insert into version values (" + NEW_VERSION + ")");
                    statement.executeUpdate("create table pic(pid integer primary key, last_send_time datetime)");
                    break;
                case 5:
                    statement.executeUpdate("alter table Scores add column live1 integer;");
                    statement.executeUpdate("alter table Scores add column live2 integer;");
                    statement.executeUpdate("alter table Scores add column live3 integer;");
                    break;
                case 6:
                    statement.executeUpdate("alter table Scores add column liveON boolean default true ;");
                    statement.executeUpdate("update version set version =" + NEW_VERSION);
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
