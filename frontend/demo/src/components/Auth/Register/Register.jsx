import styles from "./Register.module.scss";

import Routes from "../../../routes/api";
import Button from "../../Button/Button";

const Register = ({ username, password, changeField, setIsAuth }) => {
  const handleSubmit = (e) => {
    e.preventDefault();

    const routes = new Routes();
    routes.registerRequest(username, password, setIsAuth);
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

      <Button type="submit">Register</Button>
    </form>
  );
};

export default Register;
