package DataSource;

import java.util.ArrayList;

public class DataSourceList {
    public ArrayList<DataSource> list = null;

    public DataSourceList() {
        list = new ArrayList<DataSource>();
    }

    public DataSourceList(String initStr) {
        list = new ArrayList<DataSource>();

        if (!initStr.equals("")) {
            String[] strList = initStr.split(";");

            for (int i = 0; i < strList.length; ++i) {
                list.add(new DataSource(strList[i]));
            }
        }
    }
    public synchronized void addData(DataSource dataSource) {
        list.add(dataSource);
    }

    public void resetData(String initStr) {
        list = new ArrayList<DataSource>();

        if (!initStr.equals("")) {
            String[] strList = initStr.split(";");

            for (int i = 0; i < strList.length; ++i) {
                list.add(new DataSource(strList[i]));
            }
        }
    }

    public DataSource findDataByUsername (String username) {
        for (int i = list.size() - 1; i >=0; --i) {
            if (username == list.get(i).getUsername()) {
                return list.get(i);
            }
        }

        return null;
    }

    public String toString() {
        ArrayList<String> strList = new ArrayList<String>();

        for (int i = 0; i < list.size(); ++i) {
            strList.add(list.get(i).toString());
        }

        return String.join(";", strList);
    }
}
