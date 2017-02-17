package org.apereo.cas.ext.login.handle;

import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.ext.login.entity.User;
import org.apereo.cas.ext.login.repository.UserRepository;
import org.apereo.cas.ext.login.util.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;

/**
 * Created by wudongshen on 2017/2/16.
 */
public class QueryUserAuthenticationHandle extends AbstractUsernamePasswordAuthenticationHandler {

    Logger logger = LoggerFactory.getLogger(QueryUserAuthenticationHandle.class);

    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential transformedCredential) throws GeneralSecurityException, PreventedException {

        final String username = transformedCredential.getUsername();
        final String password = transformedCredential.getPassword();

        Assert.notNull(userRepository, "数据库访问异常");

        List<User> users = userRepository.findByUsername(username);

        if ( users == null ) {
            logger.warn( username + "不存在" );
            throw new FailedLoginException("用户名密码错误");

        }
        if ( users.size() > 1) {
            logger.warn( username + "存在多条记录");
            throw new FailedLoginException("用户存在多条记录");
        }

        User user = users.get(0);

        if( user.getPassword().equals(
                Md5Utils.hash(user.getUsername() + password + user.getSalt()))
        ){
            return createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username), null);
        }else {
            throw new FailedLoginException("用户名密码错误");
        }

    }
}
