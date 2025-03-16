<h1 align="center">
  Spring Learn Repo
</h1>

<div align="center">
  
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

</div>

---

## **✔️ What's already done**
- Jwt auth with access and refresh tokens (cookie)
- Auto cleanup refresh token table
- Rate limit (🗒️ need to move it to db and create auto cleanup)
- 
---

## **⭐ Existing endpoints**

<div>
  Auth

  <ul>
  <li>/api/auth/register</li>
  <li>/api/auth/login</li>
  <li>/api/auth/refresh</li>
  </ul>
</div>

<div>
  Testing
  <ul>
    <li>/api/greetings</li>
  </ul>
</div>

---

## **📝 TODO**

- Caching
- Logging
- Documentation

---

## **🚀 How to run**

- **Clone repository**
```git
git clone https://github.com/vxll03/Spring-Security-Learn
```

- **Build maven**
```maven
mvn clean install
```

- **Start project**
```maven
mvn spring-boot start
```
