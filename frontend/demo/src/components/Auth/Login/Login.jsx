import styles from "./Login.module.scss";

import Routes from "../../../routes/api";
import Button from "../../Button/Button";

const Login = ({ username, password, changeField, setIsAuth }) => {
  const handleSubmit = (e) => {
    e.preventDefault();

    const routes = new Routes();
    routes.loginRequest(username, password, setIsAuth);
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <label htmlFor="username" className={styles.label}>
        Username
      </label>
      <input
        id="username"
        type="text"
        name="username"
        value={username}
        onChange={changeField}
        className={styles.input}
      />

      <label htmlFor="password" className={styles.label}>
        Password
      </label>
      <input
        id="password"
        type="password"
        name="password"
        value={password}
        onChange={changeField}
        className={styles.input}
      />

      <Button type="submit">Log in</Button>
    </form>
  );
};

export default Login;
