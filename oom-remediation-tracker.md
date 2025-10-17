# DAO Memory Remediation Tracker

Tracking candidates where `AbstractDao.readData(...)` currently loads large result sets into memory. Each item includes the follow-up we want to implement (pagination, streaming export, or SQL aggregation).

- [x] `com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao.streamCsvVisits(...)` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/CaregiverVisitationDao.java:34`) — switched CSV export to stream rows directly instead of materialising the full table.
- [x] `com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao.countAllVisits()` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/CaregiverVisitationDao.java:70`) — now returns a single aggregate count so no large list is allocated.
- [x] `com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao.streamCsvHouseholdServices(...)` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/HouseholdServiceReportDao.java:32`) — CSV export now streams rows without building a giant list.
- [x] `com.bluecodeltd.ecap.chw.dao.VCAServiceReportDao.streamCsvServices(...)` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/VCAServiceReportDao.java:57`) — stream results instead of building a full `List`.
- [x] `com.bluecodeltd.ecap.chw.dao.VcaVisitationDao.streamCsvVisitations(...)` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/VcaVisitationDao.java:124`) — paginate export path to avoid reading every visitation at once.
- [x] `com.bluecodeltd.ecap.chw.dao.VCAScreeningDao.streamCsvVcas(...)` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/VCAScreeningDao.java:26`) — CSV export uses streaming cursor instead of materialising the list.
- [x] `com.bluecodeltd.ecap.chw.dao.CaregiverHivAssessmentDao.streamAllHivAssessments(...)` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/CaregiverHivAssessmentDao.java:36`) — streams the caregiver assessments instead of materialising them in memory.
- [ ] `com.bluecodeltd.ecap.chw.dao.HouseholdDao.getAllHouseholdInDebug()` (`opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/dao/HouseholdDao.java:308`) — guard debug helper with limits or pagination before enabling in production.
