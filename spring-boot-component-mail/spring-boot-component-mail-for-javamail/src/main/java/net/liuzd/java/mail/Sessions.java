package net.liuzd.java.mail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sessions {

    private static Session session;

    private static Store   store;

    public static Session init(String smtpHost, int port, String from, String password, boolean isDebug) {
        if (Assist.isNotEmpty(smtpHost) && port > 0) {
            Prop.put(smtpHost, port);
        }
        return init(Prop.get(), getAuthenticator(from, password), isDebug);
    }

    public static Session init(String smtpHost, int port, String from, String password) {
        Prop.put(smtpHost, port);
        return init(Prop.get(), getAuthenticator(from, password), false);
    }

    public static Session init(Properties prop, Authenticator auth, boolean isDebug) {
        Session session = Session.getDefaultInstance(prop, auth);
        session.setDebug(isDebug);
        return session;
    }

    public static Authenticator getAuthenticator(String from, String password) {
        //
        return new Authenticator() {

            //
            public PasswordAuthentication getPasswordAuthentication() {
                // 发件人邮件用户名、密码
                return new PasswordAuthentication(from, password);
            }
        };
    }

    public static synchronized Session get() {
        if (null == session) {
            session = init(null, -1, Prop.getFrom(), Prop.getPassword(), Prop.getSessionDebug());
        }
        return session;
    }

    public static synchronized Store getStore() throws MessagingException {
        if (null == store) {
            store = get().getStore(Prop.getProtocol());
            store.connect(Prop.getPop3Host(), Prop.getPop3Port(), Prop.getFrom(), Prop.getPassword());
        }
        return store;
    }

    public static Folder getFolder(POP3Param param) throws NoSuchProviderException, MessagingException {
        Folder folder = getStore().getFolder("INBOX");
        /*
         * Folder.READ_ONLY：只读权限 Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         */
        folder.open(param.getReadStats()); // 打开收件箱
        loadListener(folder);
        return folder;
    }

    public static MimeMessage getMessage() throws MessagingException {
        MimeMessage msg = new MimeMessage(get());
        // set message headers
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        return msg;
    }

    public static void loadListener(Folder folder) throws MessagingException {
        //
        folder.addConnectionListener(new ConnectionListener() {

            @Override
            public void opened(ConnectionEvent e) {
                log.info("[ConnectionListener-opened]" + e.getSource());
            }

            @Override
            public void disconnected(ConnectionEvent e) {
                log.info("[ConnectionListener-disconnected]" + e.getSource());
            }

            @Override
            public void closed(ConnectionEvent e) {
                log.info("[ConnectionListener-closed]" + e.getSource());
            }
        });
        //
        folder.addFolderListener(new FolderListener() {

            @Override
            public void folderRenamed(FolderEvent e) {
                log.info("[FolderListener-folderRenamed]" + e.getNewFolder().getFullName());
            }

            @Override
            public void folderDeleted(FolderEvent e) {
                log.info("[FolderListener-folderDeleted]" + e.getFolder().getFullName());
            }

            @Override
            public void folderCreated(FolderEvent e) {
                log.info("[FolderListener-folderCreated]" + e.getNewFolder().getFullName());
            }
        });
        //
        folder.addMessageChangedListener(new MessageChangedListener() {

            @Override
            public void messageChanged(MessageChangedEvent e) {
                log.info("[MessageChangedListener-messageChanged]" + e.getMessageChangeType());
                Message message = e.getMessage();
                //
                try {
                    MimeMessage msg = (MimeMessage) message;
                    log.info("[MessageChangedListener-messageChanged]消息序号：{}  发送时间：{}  标题：{}", msg.getMessageNumber(),
                            msg.getSentDate(), Assist.getSubject(msg));
                } catch (MessagingException | IOException e1) {
                    log.error("[MessageChangedListener-messageChanged]" + e1.getMessage());
                }
            }
        });
        //
        folder.addMessageCountListener(new MessageCountListener() {

            @Override
            public void messagesRemoved(MessageCountEvent e) {
                log.info("[messagesRemoved]" + e.getSource());
                Message[] messages = e.getMessages();
                for (Message message : messages) {
                    try {
                        MimeMessage msg = (MimeMessage) message;
                        log.info("[MessageChangedListener-messagesRemoved]消息序号：{}  发送时间：{}  标题：{}", msg
                                .getMessageNumber(), msg.getSentDate(), Assist.getSubject(msg));
                    } catch (MessagingException | IOException e1) {
                        log.error("[messagesRemoved]" + e1.getMessage());
                    }
                }
            }

            @Override
            public void messagesAdded(MessageCountEvent e) {
                log.info("[messagesAdded]" + e.getSource());
                Message[] messages = e.getMessages();
                for (Message message : messages) {
                    try {
                        MimeMessage msg = (MimeMessage) message;
                        log.info("[MessageChangedListener-messagesAdded]消息序号：{}  内容：{}  标题：{}", msg.getMessageNumber(),
                                Assist.getBody(msg), Assist.getSubject(msg));
                    } catch (MessagingException | IOException e1) {
                        log.error("[messagesAdded]" + e1.getMessage());
                    }
                }
            }
        });
    }
}
