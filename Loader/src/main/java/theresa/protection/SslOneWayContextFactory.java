package theresa.protection;


import theresa.Main;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public final class SslOneWayContextFactory {
	
	 private static final String PROTOCOL = "TLS";

    private static SSLContext CLIENT_CONTEXT;//客户端安全套接字协议

	 public static SSLContext getClientContext(InputStream caPath , String password){
		 if(CLIENT_CONTEXT!=null) return CLIENT_CONTEXT;
		 
		 InputStream tIN = null;
		 try{
			 //信任库 
			TrustManagerFactory tf = null;
			if (caPath != null) {
				//密钥库KeyStore
			    KeyStore tks = KeyStore.getInstance("JKS");
			    //加载客户端证书
			    tIN = caPath;
			    tks.load(tIN, password.toCharArray());
			    tf = TrustManagerFactory.getInstance("SunX509");
			    // 初始化信任库  
			    tf.init(tks);
			}
			 
			 CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
			 //设置信任证书
			 CLIENT_CONTEXT.init(null,tf == null ? null : tf.getTrustManagers(), null);
			 
		 }catch(Exception e){
			 Main.INSTANCE.println("Fail to init for authorization");
			 Main.INSTANCE.doCrash();
		 }finally{
			 if(tIN !=null){
					try {
						tIN.close();
					} catch (IOException e) {
						Main.INSTANCE.println("Unknown error x21");
					}
				}
		 }
		 
		 return CLIENT_CONTEXT;
	 }

}
