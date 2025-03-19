import styles from "./Auth.module.scss";

import { useState } from "react";
import Login from "./Login/Login";
import Register from "./Register/Register";

const Auth = ({ setIsAuth }) => {
  const [title, setTitle] = useState("Login");
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleTypeChange = (titleName) => {
    setTitle(titleName);
  };

  return title === "Login" ? (
    <div className={styles.container}>
      <h2 className={styles.title} onClick={() => handleTypeChange("Register")}>
        {title}
      </h2>
      <Login
        username={formData.username}
        password={formData.password}
        changeField={handleInputChange}
        setIsAuth={setIsAuth}
      />
    </div>
  ) : (
    <div className={styles.container}>
      <h2 className={styles.title} onClick={() => handleTypeChange("Login")}>
        {title}
      </h2>
      <Register
        username={formData.username}
        password={formData.password}
        changeField={handleInputChange}
        setIsAuth={setIsAuth}
      />
    </div>
  );
};

export default Auth;
