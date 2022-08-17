package com.swhy.swhypractice.utils;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.commons.io.IOUtils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;



public class SSHLinuxUtils {

    public SSHLinuxUtils() {
    }

    /**
     * 创建ssh session
     *
     * @return
     * @throws Exception
     */
    public Session createSession(final String host, final int port, final String userName, final String password)
            throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(userName, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        return session;
    }
    /**
     * 执行sh命令
     *
     * @param session session
     * @param command sh命令
     * @throws JSchException
     * @throws IOException
     */
    public List<String> exeCommand(Session session, final String command) throws Exception {
        List<String> result = new ArrayList<>();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        try {
            // channel.setPty(true);
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            InputStream inputStream = channel.getInputStream();
            BufferedInputStream in = new BufferedInputStream(inputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("GBK")));
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }

            in.close();
            br.close();

        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return result;

    }

    /**
     * 获取Java进程id
     *
     * @param session  session
     * @param command  sh命令
     * @return 进程id
     * @throws Exception
     */
    /**
    public static String getPid(Session session, final String command, int port) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        String pid = null;
        try {
            InputStream inputStream = channel.getInputStream();
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            pid = IOUtils.toString(inputStream, "UTF-8");
            String[] split = pid.split("\n");
            for (String s : split) {
                if(s.contains("--"+port+"--")){
                    System.out.println(s);
                    pid=s.substring(0,16).split("root")[1].trim();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return pid;

    }
    */


    /**
     * 获取Java进程id
     *
     * @param session  session
     * @param command  sh命令
     *
     * @return 进程id
     * @throws Exception
     */
    /**
    public static String getPid(Session session, final String command, String topicGroup) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        String pid = null;
        try {
            InputStream inputStream = channel.getInputStream();
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();
            String text = IOUtils.toString(inputStream, "UTF-8");
            String[] split = text.split("\n");
            for (String sh : split) {
                if(sh.contains("-jar")) {
                    sh=sh.substring(0,sh.indexOf("-jar")).trim();
                    if(sh.endsWith(topicGroup.trim())){
                        System.out.println(sh);
                        pid=sh.substring(0,16).split("root")[1].trim();
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return pid;

    }
    */


    /**
     * 上传文件
     *
     * @param session  session
     * @param fileDest 文件地址
     * @throws Exception
     */
    public static void upload(Session session, final String fileDest) throws Exception {
        ChannelSftp channel = ((ChannelSftp) session.openChannel("sftp"));
        try {
            channel.connect();
            channel.put(new FileInputStream("/start.sh"), fileDest);
            // 添加权限
            channel.chmod(777, fileDest);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public static void deleteDir(Session session, final String fileDest) throws Exception {
        ChannelSftp channel = ((ChannelSftp) session.openChannel("sftp"));
        try {
            channel.connect();
            channel.rmdir(fileDest);
            // 添加权限
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    /**
     * 写入命令到start.sh
     *
     * @param sh 获取当前进程运行以来的配置文件地址
     */
    public static void createTempFile(String createFileDir, String sh) throws Exception {
        File file = new File(createFileDir);
        file.createNewFile();
        BufferedWriter br = new BufferedWriter(new FileWriter(file));
        try {
            br.write(sh);
        } finally {
            br.flush();
            br.close();
        }
    }

    /**
     * 根据路径回去文件名称
     *
     * @param session
     * @param dir     获取文件名称的路径
     * @return
     * @throws JSchException
     */
    @SuppressWarnings("unchecked")
    public static String getFileNameByDir(Session session, String dir) throws JSchException {
        ChannelSftp channel = ((ChannelSftp) session.openChannel("sftp"));
        try {
            channel.connect();
            Vector<ChannelSftp.LsEntry> vector = channel.ls(dir);
            Iterator<ChannelSftp.LsEntry> iterator = vector.iterator();
            while (iterator.hasNext()) {
                ChannelSftp.LsEntry next = iterator.next();
                String filename = next.getFilename();
                if (filename.endsWith(".jar")) {
                    return filename;
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return null;
    }
}