import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import entity.DataList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Demo class
 *
 * @author Benny Shi
 * @date 2018/10/20
 */
public class Main {
    public static void main(String[] args) {
            ArtData("./bcdataSentence1.txt");
    }

    private static DataList GetBMList(int i){
        Gson gson = new Gson();
        String listBody =  HttpRequest.get("https://app.blockmeta.com/w1/news/list?num=100&page="+i).body();
        DataList dataList = gson.fromJson(listBody, DataList.class);
        return dataList;
    }

    private static void ArtData(String pathName) {
        FileWriter fw = null;
        File fSentence = new File(pathName);
        try {
            fw = new FileWriter(fSentence, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        for (int page = 0; page < 230; page++){
            DataList dataList = GetBMList(page);
            List<DataList.ListBean> list = dataList.getList();
            for(int i = 0; i < list.size(); i++){
                DataList.ListBean listBean = list.get(i);
                System.out.println(page+"页---"+listBean.getId()+"----"+listBean.getTitle());
                    Connection con = Jsoup.connect("https://www.8btc.com/article/"+listBean.getId());
                    //请求头设置
                    con.header("Accept", "text/html, application/xhtml+xml, */*");
                    con.header("Content-Type", "application/x-www-form-urlencoded");
                    con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))");
                    //解析请求结果
                    Document document= null;
                try {
                    document = con.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    Elements element = null;
                    try {
                        element = document.getElementsByClass("bbt-html").first().getElementsByTag("p");
                        for (int e = 0; e < element.size(); e++){
                            if(element.get(e).text().length() != 0){
                                System.out.println(element.get(e).text());
                                pw.println(element.get(e).text());
                                pw.flush();
                            }
                        }
                    }catch (Exception e){
                        System.out.println(e);
                    }
            }
        }

        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
