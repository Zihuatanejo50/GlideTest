package com.nelson.glidetest.network;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

/**
 * 安全的OkHttpClient（创建OkHttpClient检查SSL证书检查）
 *
 * 自己实现网络栈，接受自签名证书，以12306.cn为例
 * Created by Nelson on 2018/4/17.
 */

public class SafeOkHttpClient {

    /**
     * 以流的方式添加信任证书，初始化HTTPS
     */
    public static OkHttpClient getsafeOkHttpClient(Context context) {
        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        final InputStream inputStream;
        try {
            inputStream = context.getAssets().open("srca.cer"); // 得到证书的输入流
            try {

                trustManager = trustManagerForCertificates(inputStream);//以流的方式读入证书
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                sslSocketFactory = sslContext.getSocketFactory();


            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();
            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a {@code
     * SSLHandshakeException}. <p> <p>This can be used to replace the host platform's built-in
     * trusted certificates with a custom set. This is useful in development where certificate
     * authority-trusted certificates aren't available. Or in production, to avoid reliance on
     * third-party certificate authorities. <p> <p> <h3>Warning: Customizing Trusted Certificates is
     * Dangerous!</h3> <p> <p>Relying on your own trusted certificates limits your server team's
     * ability to update their TLS certificates. By installing a specific set of trusted
     * certificates, you take on additional operational complexity and limit your ability to migrate
     * between certificate authorities. Do not use custom trusted certificates in production without
     * the blessing of your server's TLS administrator.
     */
    public static X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory
                .generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }


    /**
     * 添加password
     */
    public static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // 这里添加自定义的密码，默认
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
