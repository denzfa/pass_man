Requirements:
- Create new Spring Boot Maven project in current directory - backend resource server (API ONLI)

- Use Java 25 and last stable version of any required component.

- Use http://localhost:9000 as Authorization server - OAuth 2.1 with the default endpoints

- Only Authentificated users with the ROLE_ADMIN can reach any of the current app endpoint

- Custom errors handling should be implemented with the standard response codes and JSON body 

- Implement CRUD for all model entries

- Prefer configuration over custom code. Keep the implementation minimal while remaining production-grade.

- Minimal top-level tests coverage should be implemented.

- In case of contradiction or uncertainty ask me.



Model (UUID is a primary key):
- Document
   ~ Name (CHAR30) Unique, but not PK
   ~ Title (TEXT100, Language-depended)
   ~ Description (TEXT255, Language-depended)
   ~ createdAt
   ~ modifiedAt
   ~ createdBy
   ~ modifiedBy


Development Parameters:
- Namespace: denzfa.cockpit.passman
- Project Name: Passwords Manager


