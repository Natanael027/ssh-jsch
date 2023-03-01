package com.dart.ssh.config;

import com.google.common.base.Joiner;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Configurasi SSH server saat running dan fungsi dari tiap command.
 * @Slf4j - Membuat log saat fungsi aplikasi berjalan, kebanyakan info.
 * @Configuration - Membuat ssh client dijalankan pada layer configuration.
 */
@Slf4j
//@AllArgsConstructor
//@NoArgsConstructor
@Configuration
public class SSHClientConfiguration {

    /**
     * @param {session} Session - Untuk memulai sesi ssh client saat akan connect ke server.
     * @param {string} finalResult - Output akhir dari command, mengoper output dari hasil cmd ke string.
     * @return {string} finalResult dioper ke controller untuk ditampilkan pada postman.
     */
    public Session session;
    public String finalResult;

    /**
     * Fungsi untuk membuat session ke server, menggunakan @session berisikan param @username, @hostname, @port dan @password dari controller
     * @param {string} username - username untuk login ke server ssh.
     * @param {string} hostname - alamat server ssh, berbentuk url.
     * @param {integer} port - port alamat server ssh.
     * @param {string} password - password untuk login ke server ssh.
     * @log jika berhasil maka akan menampilkan ouput "SSHClient is connected".
     */
    public void connect(String username, String hostname, Integer port, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, hostname, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(30000);
        log.info("SSHClient is connected");
    }

    /**
     * Fungsi untuk memulai session client
     * @param - param connect berasal fungsi sebelumnya yaitu connect.
     * @log jika berhasil maka akan menampilkan ouput "SSHClient is started".
     */
    public void startClient() throws JSchException {
        session.connect();
        log.info("SSHClient is started...");
    }

    /**
     * Fungsi untuk mengakhiri session client
     * @log jika berhasil maka akan menampilkan ouput "SSHClient is stopped...".
     */
    public void stopClient() {
        session.disconnect();
        log.info("SSHClient is stopped...");
    }

    /**
     * Fungsi untuk mengeksekusi command pada server server, menggunakan @param command dan @param path yang berasal dari controller.
     * @param {string} command - perintah untuk di eksekusi pada channel exec, berisi perintah perintah linux.
     * @param {string} path - letak atau lokasi untuk mengeksekusi command, misalnya membuat folder/menghapus file.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * @return {string} finalResult berasal dari responseString yang dikonversi dari ByteArrayOutputStream CMD menjadi String.
     * ouput berbentuk json string karena menggunakan parameter jq dalam command dan pengelompokan header dan range tertentu.
     */
    public String executeCommand(String command, String path) {
        try {
            Channel channel = session.openChannel("exec");
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            ((ChannelExec) channel).setCommand("cd " + path + ";" + command +"| jq -sR '[sub(\"\\n$\";\"\") | splits(\"\\n\") | sub(\"^ +\";\"\") | [splits(\" +\")]] | .[0] as $header | .[1:] | [.[] | [. as $x | range($header | length) | {\"key\": $header[.], \"value\": $x[.]}] | from_entries]'") ;
            channel.connect();
            while (channel.isConnected()) {
                Thread.sleep(200);
            }
            String responseString = new String(responseStream.toByteArray());
            finalResult = responseString;
            channel.disconnect();
            System.out.println("Disconnected channel " + channel.getExitStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalResult;
    }

    public String newCommand(String command) {
        try {
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            channel.setInputStream(null);

            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in=channel.getInputStream();
            channel.connect();
            while (channel.isConnected()) {
                Thread.sleep(2000);
            }
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
//                    System.out.print(new String(tmp, 0, i));
                    Thread.sleep(2000);
                    String responseString = new String(tmp, 0, i);
                    finalResult = responseString;
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            System.out.println("Disconnected channel " + channel.getExitStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("config >> "+finalResult);
        return finalResult;
    }

    public String putFile(String localFile, String remoteDirFile){
        try{
            Channel sftp  = session.openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // transfer file from local to remote server
            channelSftp.put(localFile, remoteDirFile);

            // download file from remote server to local
//            channelSftp.get(remoteFile, localDir + "here.txt");

            channelSftp.exit();
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }
    }

 public String getFile(String remoteFile, String localDirFile){
        try{
            Channel sftp  = session.openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // transfer file from local to remote server
//            channelSftp.put(localFile, remoteDirFile);

            // download file from remote server to local
            channelSftp.get(remoteFile, localDirFile );

            channelSftp.exit();
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }

    }


    /**
     * Fungsi untuk mengeksekusi command pm2 pada server, menggunakan @param command dan @param path yang berasal dari controller.
     * @param {string} command - perintah untuk di eksekusi pada channel exec, berisi perintah perintah pm2.
     * @param {string} path - letak atau lokasi untuk mengeksekusi command, berisi lokasi dari file project aplikasi.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * @return {string} finalResult berasal dari result yang dikonversi dari ByteObject CMD menjadi String.
     * ouput berbentuk string karena tidak dapat dikonversi mengguanakan jq.
     */
    public String pm2Command(String path, String command) {
        try {
            String result = null;
//            String command = Joiner.on("\n").withKeyValueSeparator("=").join(config);
            /*String command = Splitter.on("-").trimResults().splitToList(config);*/
            System.out.println(command);

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("cd " + path + ";" +command);
//            ((ChannelExec) channel).setCommand("cd ../home;rm .env;printf " + "\"" +command+ "\" + >> .env;cat .env");

            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
//            channel.setOutputStream(System.out, true);

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            while (channel.isConnected()) {
                Thread.sleep(1000);
            }
            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;
            }
            channel.disconnect();
            System.out.println("Disconnected channel " + channel.getExitStatus());
            finalResult = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalResult;

    }

    /**
     * Fungsi untuk membuat file configurasi berbentuk text file pada server, menggunakan @param config, @param path, @param filename yang berasal dari controller.
     * @param {Map<String, Object>} config - perintah untuk menampung isi file configurasi, dibuat dengan dipisahkan melalui baris baru (\n) dan ditambahkan (:), di eksekusi pada channel exec.
     * @param {string} path - letak atau lokasi untuk mengeksekusi command, berisi lokasi folder yang akan di buat file tersebut.
     * @param {string} filename - nama file konfigurasi, berbentuk string.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * @return {string} finalResult berasal dari result yang dikonversi dari ByteObject CMD menjadi String. Berisi file configurasi yang dibuat
     * ouput berbentuk map string isi file config/text (tidak dapat dikonversi mengguanakan jq).
     */
    public String createConfigFile(Map<String, Object> config, String path, String filename) {
        try {
            String result = null;
            String command = Joiner.on("\n").withKeyValueSeparator(":").join(config);
            /*String command = Splitter.on("-").trimResults().splitToList(config);*/
            System.out.println(command);

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("cd " + path + ";rm "+filename +";printf " + "\"" +command+ "\" + >> " + filename +";cat "+ filename);
//            ((ChannelExec) channel).setCommand("cd ../home;rm .env;printf " + "\"" +command+ "\" + >> .env;cat .env");

            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
//            channel.setOutputStream(System.out, true);

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;

            }
            channel.disconnect();
            System.out.println("Disconnected channel " + channel.getExitStatus());
            finalResult = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalResult;

    }

    /**
     * Fungsi untuk membuat file ecosytem pm2 berbentuk file js ex : ecosystem.config.js pada server, menggunakan @param name, @param path, @param filename, @param script, @param watch dan @param env, env_production, env_test, dan env_development yang berasal dari controller.
     * @param {string} path - letak atau lokasi untuk mengeksekusi command, berisi lokasi folder yang akan di buat file ecosystem tersebut.
     * @param {string} filename - nama file ecosystem, berbentuk string.
     * @param {string} name - nama aplikasi pada file ecosystem.
     * @param {string} script - letak atau lokasi dari file app.js atau aplikasi yang sudah di built (file utama start aplikasi).
     * @param {string} watch - param untuk mengaktifkan fitur watch (memantau jika ada perubahan pada file atau folder aplikasi).
     * @param {Map<String, Object>} env - perintah untuk menampung isi env, dibuat dengan dipisahkan melalui baris baru (\n), spasi (\) dan ditambahkan (: \), di tambahkan pada fie ecosystem dengan {}.
     * @param {Map<String, Object>} env_production - perintah untuk menampung isi env_production, dibuat dengan dipisahkan melalui baris baru (\n), spasi (\) dan ditambahkan (: \), di tambahkan pada fie ecosystem dengan {}.
     * @param {Map<String, Object>} env_test - perintah untuk menampung isi env_test, dibuat dengan dipisahkan melalui baris baru (\n), spasi (\) dan ditambahkan (: \), di tambahkan pada fie ecosystem dengan {}.
     * @param {Map<String, Object>} env_development - perintah untuk menampung isi env_development, dibuat dengan dipisahkan melalui baris baru (\n), spasi (\) dan ditambahkan (: \), di tambahkan pada fie ecosystem dengan {}.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * @return {string} finalResult berasal dari result yang dikonversi dari ByteObject CMD menjadi json string. Berisi file ecosystem yang dibuat
     * ouput berbentuk map string isi file ecosystem (dibuat dengan jq berbentuk js).
     */
    public String createEcoFile(String path, String filename, String name, String script, String watch, Map<String, Object> env, Map<String, Object> env_production, Map<String, Object> env_test, Map<String, Object> env_development) {
        try {
            String result = null;
//            System.out.println(env);
            String env1 = Joiner.on("\",\n").withKeyValueSeparator(": \"").join(env);
            System.out.println(env1);

//            System.out.println(env_production);
            String env2 = Joiner.on("\",\n").withKeyValueSeparator(": \"").join(env_production);
//            System.out.println(env2);

//            System.out.println(env_test);
            String env3 = Joiner.on("\",\n").withKeyValueSeparator(": \"").join(env_test);
//            System.out.println(env3);

//            System.out.println(env_development);
            String env4 = Joiner.on("\",\n").withKeyValueSeparator(": \"").join(env_development);
//            System.out.println(env4);

            /*String command = Splitter.on("-").trimResults().splitToList(config);*/
//            System.out.println(command);

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("cd " + path + ";rm "+filename +";jq -n '{apps: [ {name: "+ "\"" + name + "\"" +", script: "+ "\"" + script + "\"" +", watch:"+ "\"" + watch + "\"" +", env: {"+"\n"+ env1 + "\"" +"}," + "env_production: {"+"\n"+ env2 + "\"" +"}," + "env_test: {"+"\n"+ env3 + "\"" +"}," + "env_development: {"+"\n"+ env4 + "\"" +"} }]}' > " + filename +";cat "+ filename);
//            ((ChannelExec) channel).setCommand("cd " + path + ";rm "+filename +";jq -n '{apps: [ {name: "+ "\"" + name + "\"" +", script: "+ "\"" + script + "\"" +", watch:"+ "\"" + watch + "\"" +", env:{"+"\n"+ env1 + "\"" +"} }]}' > " + filename +";cat "+ filename);

            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
//            channel.setOutputStream(System.out, true);

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;

            }
            channel.disconnect();
            System.out.println("Disconnected channel " + channel.getExitStatus());
            finalResult = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalResult;

    }

    /**
     * Fungsi untuk mengeksekusi penampil status server pada glances.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * status yang akan ditampilkan akan masuk kedalam database tabel detail berbentuk statistik tiap component selama waktu tertentu.
     */
    public String getrestful() {
        try {
            String result = null;

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("glances --export restful");

            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
            /*channel.setOutputStream(System.out);*/

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;

            }
/*            channel.disconnect();*/
            System.out.println("Disconnected channel " + channel.getExitStatus());
/*            finalResult = result;*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * Fungsi untuk merestart server.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * command belum di tes karna pada docker tidak dapat di restart dengan param biasa".
     */
    public String restartServer() {
        try {
            String result = null;

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("/sbin/reboot");

            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
            /*channel.setOutputStream(System.out);*/

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;
            }
            channel.disconnect();
            System.out.println("Disconnected channel " + channel.getExitStatus());
            finalResult = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalResult;

    }

    /**
     * Fungsi untuk menembak server web glances.
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * output yang ditampilkan hanya sekali tembak (satu json) dan tidak dimasukkan kedalam tabel database.
     */
    public void startGlances() {
        try {
            /*String result = null;*/
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("glances -w --disable-webui -p 21");
            /*Thread.sleep(15000);*/

            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
            /*channel.setOutputStream(System.out);*/

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
/*            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;

            }*/

/*            channel.disconnect();*/
            System.out.println("Glances Server Started...");
            Thread.sleep(1000L);
/*            finalResult = result;*/
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        return finalResult;*/

    }
    /**
     * Fungsi untuk mengakhiri glances (baik rest api atau web server).
     * @sout jika berhasil maka akan menampilkan ouput "Disconnected channel ".
     * jika berhasil output yang ditampilkan "Glances Server Shutdown".
     */
    public void endGlances() {
        try {
            /*String result = null;*/
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("pkill glances");
            /*Thread.sleep(15000);*/
            /*channel.setInputStream(System.in);*/
            channel.setInputStream(null);// this method should be called before connect()
            /*channel.setOutputStream(System.out);*/

            ((ChannelExec) channel).setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();
/*            byte[] byteObject = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int readByte = inputStream.read(byteObject, 0, 1024);
                    if (readByte < 0)
                        break;
                    result = new String(byteObject, 0, readByte);
                }
                if (channel.isClosed())
                    break;

            }*/
            channel.disconnect();
            System.out.println("Glances Server Shutdown");
/*            finalResult = result;*/
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        return finalResult;*/

    }
}
