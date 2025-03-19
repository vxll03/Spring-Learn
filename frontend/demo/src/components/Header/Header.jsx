import styles from "./Header.module.scss";

import { Link } from "react-router-dom";

const Header = ({ isAuth, handleLogout }) => {
  return (
    <div className={styles.header}>
      <h1>Header</h1>
      <nav>
        <Link to="/"></Link>
        {!isAuth ? (
          <Link to="/auth">Auth</Link>
        ) : (
          <a onClick={handleLogout} className={styles.logout}>
            Logout
          </a>
        )}
      </nav>
    </div>
  );
};

export default Header;
