
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TranslationApp {
    private JFrame frame;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TranslationApp().createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        // 创建主窗口
        frame = new JFrame("中英文翻译应用");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // 创建输入文本区域
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);

        // 创建滚动面板，用于容纳输入文本区域
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setPreferredSize(new Dimension(200, 100));

        // 创建输出文本区域
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        // 创建滚动面板，用于容纳输出文本区域
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setPreferredSize(new Dimension(200, 100));

        // 创建翻译按钮
        JButton translateButton = new JButton("翻译");
        translateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                translate();
            }
        });

        // 创建布局
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());  // 使用 GridBagLayout 布局管理器
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // 设置组件之间的间距

        // 添加输入文本区域
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JLabel("输入文本:"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(inputScrollPane, gbc);

        // 添加翻译按钮
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(translateButton, gbc);

        // 添加翻译结果标签
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JLabel("翻译结果:"), gbc);

        // 添加输出文本区域
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(outputScrollPane, gbc);

        // 添加布局到主窗口
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }


    private void translate() {
        String text = inputTextArea.getText();

        // 发送翻译请求并获取结果
        String translation = getTranslation(text);
        translation = getRealTranslation(translation);

        // 更新输出文本区域
        outputTextArea.setText(translation);
    }

    private String getTranslation(String text) {
        String url = "http://fy.webxml.com.cn/webservices/EnglishChinese.asmx/TranslatorString?wordKey=" + text;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            // 检查响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        return result.toString();
                    }
                }
            } else {
                System.err.println("HTTP请求失败，状态码：" + statusCode);
            }
        } catch (IOException e) {
            System.err.println("发生IO异常：" + e.getMessage());
        }

        return "";
    }

    private String getRealTranslation(String str) {
        String xmlResponse = str;
        System.out.println(str);
        try {
            // 创建XML解析器工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 创建XML解析器
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 将XML字符串转换为输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes());

            // 解析XML输入流
            Document document = builder.parse(inputStream);

            // 获取所有<string>元素
            NodeList stringNodes = document.getElementsByTagName("string");

            Element stringElement = (Element) stringNodes.item(3);
            String content = stringElement.getTextContent();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "翻译失败";
    }

}