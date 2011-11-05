package controllers;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import models.User;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import play.cache.Cache;
import play.libs.OAuth;
import play.libs.OAuth.ServiceInfo;
import play.libs.OAuth.TokenPair;
import play.libs.WS;
import play.mvc.Scope.Session;
import play.modules.gae.*;

public class Oauth extends Application {

    private static final ServiceInfo TWITTER = new ServiceInfo(
            "http://twitter.com/oauth/request_token",
            "http://twitter.com/oauth/access_token",
            "http://twitter.com/oauth/authorize",
            "1WBmR6GLyfghlT43VoGtQw",
            "M3ujw1fCAwXgU1apkB2KoqBpmVjeYby4f6ZovVWJvZ8");

    public static void googleConnect() {
        if (GAE.isLoggedIn()) {
            if(User.findByEmail(GAE.getUser().getEmail()) == null) {
            	User user = new User(GAE.getUser().getEmail());
                user.insert();
                Session.current().put("logged", user.id);
                redirect("/");
            } else {
            	User user = User.findByEmail(GAE.getUser().getEmail());
                Session.current().put("logged", user.id);
                redirect("/");
            }
        } else {
            GAE.login("Oauth.googleConnect");
        }
    }

    public static void twitterConnect() {
        if (OAuth.isVerifierResponse()) {
            TokenPair tokens = OAuth.service(TWITTER).requestAccessToken( new TokenPair((String) Cache.get("token_token" + session.getId()), (String) Cache.get("token_secret" + session.getId())));

            String url = "http://api.twitter.com/1/account/verify_credentials.xml";

            String accInfo = "";
            try {
                accInfo = WS.url(url).oauth(TWITTER, tokens).get().getString();
            } catch (Exception e) {
                Application.index();
            }

            // parse xml result
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(accInfo));
                Document doc = db.parse(is);
                String twitterId = doc.getElementsByTagName("id").item(0).getTextContent();
                String twitter = doc.getElementsByTagName("name").item(0).getTextContent();

                if(Application.connectedUser() == null) {
                    if(User.findByTwitterId(Long.parseLong(twitterId)) == null) {
                    	User user = new User(twitter, Long.parseLong(twitterId));
                        user.insert();
                        Session.current().put("logged", user.id);
                    } else {
                    	User user = User.findByTwitterId(Long.parseLong(twitterId));
                        Session.current().put("logged", user.id);
                    }
                    Application.index();
                } else {
                    Application.index();
                }

            } catch (Exception e) {
                play.Logger.error("error parsing twitter login result", e);

            }

        }

        OAuth twitt = OAuth.service(TWITTER);
        TokenPair tokens = twitt.requestUnauthorizedToken();
        Cache.set("token_secret" + session.getId(), tokens.secret, "15min");
        Cache.set("token_token" + session.getId(), tokens.token, "15min");
        redirect(twitt.redirectUrl(tokens));
    }
}
