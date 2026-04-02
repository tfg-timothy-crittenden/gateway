# Implementation Checklist

Use this checklist to guide your implementation step-by-step.

## Phase 1: Understand (15 minutes)
- [ ] Read `README.md` 
- [ ] Review `QUICK_START.md`
- [ ] Look at diagrams in `ARCHITECTURE.md`

## Phase 2: Prepare Docker (10 minutes)
- [ ] Verify Docker Desktop is running
- [ ] Verify `docker-compose` is available: `docker-compose --version`
- [ ] Verify available ports: 8080, 8081, 8082, 8083, 8761

## Phase 3: Start Docker Services (5 minutes)
```powershell
cd D:\programming-projects\tfg\tfg-gateway
docker-compose up --build
```

- [ ] Eureka Server starts (look for "Started EurekaServerApplication")
- [ ] Gateway starts (look for "Started TfgGatewayApplication")
- [ ] Both services are healthy
- [ ] No critical errors in logs

**Expected output:**
```
tfg-eureka is running
tfg-gateway is running
```

## Phase 4: Configure USER-SERVICE (5 minutes)

### Option A: Replace entire config
1. [ ] Copy `USER-SERVICE-application.yml` from gateway folder
2. [ ] Replace `<USER-SERVICE-PATH>/src/main/resources/application.yml` with it
3. [ ] **IMPORTANT:** Verify `server.port=8082` in your config

### Option B: Add Eureka config to existing file
1. [ ] Open `<USER-SERVICE-PATH>/src/main/resources/application.yml`
2. [ ] Add this section:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    ip-address: localhost
    statusPageUrl: http://localhost:8082/actuator/health
    healthCheckUrl: http://localhost:8082/actuator/health
    homePageUrl: http://localhost:8082/
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30

management:
  endpoints:
    web:
      exposure:
        include: "*"
```

- [ ] Verify spring.application.name is set (case matters!)
- [ ] Verify server.port matches (8082 for USER-SERVICE)

## Phase 5: Start USER-SERVICE (5 minutes)

```powershell
cd <USER-SERVICE-PATH>
mvn spring-boot:run
```

- [ ] Service starts successfully
- [ ] No errors in logs
- [ ] Service reaches Eureka (look for "InstanceInfoReplicator started")
- [ ] Service health endpoint works: `curl http://localhost:8082/actuator/health`

## Phase 6: Verify in Eureka (2 minutes)

Open browser: http://localhost:8761/

- [ ] Eureka dashboard loads
- [ ] See "USER-SERVICE" in the list
- [ ] Status shows "UP (1)" in green
- [ ] Instance count shows 1

## Phase 7: Test Gateway Routing (2 minutes)

Test endpoint (adjust if your USER-SERVICE has different endpoint):
```powershell
curl http://localhost:8080/users/api/users/1
```

Expected:
- [ ] Request succeeds (200 or appropriate status)
- [ ] Response comes from USER-SERVICE
- [ ] No 404 errors
- [ ] Check USER-SERVICE logs - you should see the request

## Phase 8: Repeat for EXAM-SERVICE (10 minutes)

Same process with EXAM-SERVICE:

### Configuration
- [ ] Copy or add Eureka config
- [ ] Set spring.application.name = EXAM-SERVICE
- [ ] Set server.port = 8081 (or your actual port)
- [ ] Update statusPageUrl/healthCheckUrl to port 8081

### Start
- [ ] Run: `mvn spring-boot:run`
- [ ] Verify in logs: Eureka registration successful

### Verify
- [ ] Appears in Eureka dashboard (http://localhost:8761/)
- [ ] Test: `curl http://localhost:8080/tests/api/tests/1`

## Phase 9: Repeat for CLASSROOM-SERVICE (10 minutes)

Same process with CLASSROOM-SERVICE:

### Configuration
- [ ] Copy or add Eureka config
- [ ] Set spring.application.name = CLASSROOM-SERVICE
- [ ] Set server.port = 8083 (or your actual port)
- [ ] Update statusPageUrl/healthCheckUrl to port 8083

### Start
- [ ] Run: `mvn spring-boot:run`
- [ ] Verify in logs: Eureka registration successful

### Verify
- [ ] Appears in Eureka dashboard
- [ ] Test: `curl http://localhost:8080/classrooms/api/classrooms/1`

## Phase 10: Comprehensive Testing (10 minutes)

Verify all three routes work:
```powershell
# Test USER-SERVICE
curl http://localhost:8080/users/api/users/1
- [ ] Works

# Test EXAM-SERVICE  
curl http://localhost:8080/tests/api/tests/1
- [ ] Works

# Test CLASSROOM-SERVICE
curl http://localhost:8080/classrooms/api/classrooms/1
- [ ] Works
```

Check Eureka dashboard: http://localhost:8761/
- [ ] All 3 services listed
- [ ] All show "UP (1)"
- [ ] No DOWN or OUTOFSERVICE

## Phase 11: Advanced Verification (optional)

### Check Gateway Logs
```powershell
docker logs tfg-gateway
```
- [ ] See routes matched
- [ ] See requests forwarded
- [ ] No errors

### Check Service Registrations
```powershell
curl http://localhost:8761/eureka/apps
```
- [ ] Returns XML/JSON with all 3 services
- [ ] All have status UP

### Monitor Service Health
```powershell
curl http://localhost:8082/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8083/actuator/health
```
- [ ] All return: `{"status":"UP"}`

## Phase 12: Cleanup & Documentation

- [ ] Document actual port numbers used
- [ ] Note any custom configuration changes
- [ ] Save screenshots of Eureka dashboard
- [ ] Test all endpoints one more time

## Troubleshooting Guide

### Service won't start
```
Issue: Exception during Eureka registration
Solution:
  1. Check network: curl http://localhost:8761/eureka/apps
  2. Check config: verify eureka.client.service-url
  3. Check logs: Look for Eureka errors
  4. Verify Java/Maven: java -version, mvn -version
```

### Service appears but returns 404
```
Issue: Gateway gets 404 from service
Solution:
  1. Test service directly: curl http://localhost:PORT/api/endpoint
  2. If direct works: check route config in GatewayConfig.java
  3. If direct fails: service has different endpoint
  4. Check service logs for errors
```

### Service doesn't appear in Eureka
```
Issue: Service registered but not showing
Solution:
  1. Wait 10+ seconds (lease interval)
  2. Refresh Eureka dashboard
  3. Check service logs: "InstanceInfoReplicator" messages
  4. Verify spring.application.name is uppercase
```

### Port already in use
```
Issue: Port conflicts
Solution:
  powershell:
  netstat -ano | findstr :8080
  netstat -ano | findstr :8081
  
  Then kill process:
  taskkill /PID <PID> /F
```

### Docker won't start
```
Issue: docker-compose fails
Solution:
  1. Check Docker is running: docker ps
  2. Rebuild: docker-compose build --no-cache
  3. Check logs: docker-compose logs
  4. Check ports: netstat -ano | findstr :8761
```

## Success Indicators ✅

When complete, you should see:

1. **Docker Container Status**
   ```
   CONTAINER ID   IMAGE      STATUS
   xxxx           tfg-eureka   Up 2 minutes
   xxxx           tfg-gateway  Up 1 minute
   ```

2. **Eureka Dashboard**
   ```
   http://localhost:8761/
   
   INSTANCES CURRENTLY REGISTERED WITH EUREKA
   EXAM-SERVICE (1)        UP (1) - localhost:exam-service:8081
   USER-SERVICE (1)        UP (1) - localhost:user-service:8082
   CLASSROOM-SERVICE (1)   UP (1) - localhost:classroom-service:8083
   ```

3. **Gateway Routes**
   ```
   GET http://localhost:8080/users/api/users/1 → 200 OK
   GET http://localhost:8080/tests/api/tests/1 → 200 OK
   GET http://localhost:8080/classrooms/api/classrooms/1 → 200 OK
   ```

4. **Service Logs**
   ```
   USER-SERVICE logs show requests coming from gateway
   EXAM-SERVICE logs show requests coming from gateway
   CLASSROOM-SERVICE logs show requests coming from gateway
   ```

---

## Time Estimate

| Phase | Time |
|-------|------|
| 1. Understand | 15 min |
| 2. Prepare | 10 min |
| 3. Docker | 5 min |
| 4. USER-SERVICE Config | 5 min |
| 5. USER-SERVICE Start | 5 min |
| 6. Eureka Verify | 2 min |
| 7. Test Gateway | 2 min |
| 8. EXAM-SERVICE | 10 min |
| 9. CLASSROOM-SERVICE | 10 min |
| 10. Full Testing | 10 min |
| **Total** | **74 min** |

**Actual time may vary based on download speeds and system performance.**

---

## Post-Deployment

Once everything is working:

1. [ ] Read `SETUP.md` for maintenance
2. [ ] Bookmark `ARCHITECTURE.md` for reference
3. [ ] Create backup of working configs
4. [ ] Document any custom changes
5. [ ] Plan service migration to Docker (if desired)

---

## Support

If stuck on any step:
1. Check the specific section in SETUP.md
2. Review ARCHITECTURE.md for concepts
3. Check service logs for error messages
4. Refer to "Troubleshooting Guide" above

**You've got this!** 🚀

