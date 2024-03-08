package com.ibg.receipt.shiro;

import com.ibg.receipt.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/25 23:02
 */
@Component
@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    @Resource
    private RedisTemplate<String, Session> redisTemplate;

    /**
     * The Redis key prefix for the sessions
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    /**
     * save session
     *
     * @param session
     * @throws UnknownSessionException
     */
    private void saveSession(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }

        String key = getStringKey(session.getId());
        redisTemplate.opsForValue().set(key, session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        redisTemplate.delete(this.getStringKey(session.getId()));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<Session>();

        Set<String> keys = redisTemplate.keys(ShiroConstant.MANAGEMENT_KEY_PREFIX + "*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Session s = redisTemplate.opsForValue().get(key);
                sessions.add(s);
            }
        }

        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.error("session id is null");
            return null;
        }

        Session s = (Session) redisTemplate.opsForValue().get(this.getStringKey(sessionId));
        return s;
    }

    /**
     * 获得byte[]型的key
     *
     * @param sessionId
     * @return
     */
    private byte[] getByteKey(Serializable sessionId) {
        String preKey = ShiroConstant.MANAGEMENT_KEY_PREFIX + sessionId;
        return preKey.getBytes();
    }

    /**
     * 获取String型的key
     *
     * @param sessionId
     * @return
     */
    private String getStringKey(Serializable sessionId) {
        String preKey = ShiroConstant.MANAGEMENT_KEY_PREFIX + sessionId;
        return preKey;
    }

    /**
     * Returns the Redis session keys prefix.
     *
     * @return The prefix
     */
    public String getKeyPrefix() {
        return ShiroConstant.MANAGEMENT_KEY_PREFIX;
    }

}
