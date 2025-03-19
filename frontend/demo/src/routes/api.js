import axios from "axios";
import Cookies from "js-cookie";

class Routes {
  URL = "http://localhost:8080";

  async loginRequest(username, password, setIsAuth) {
    try {
      await axios.post(
        `${this.URL}/api/auth/login`,
        {
          username: username,
          password: password,
        },
        {
          withCredentials: true,
        },
      );

      Cookies.set("isAuth", true, { expires: 7 });
      setIsAuth(true);
    } catch (error) {
      console.log(`Error caught: ${error}`);
      throw error;
    }
  }

  async registerRequest(username, password, setIsAuth) {
    try {
      const response = await axios.post(`${this.URL}/api/auth/register`, {
        username: username,
        password: password,
      });
      if (response.status >= 200 && response.status < 300) {
        await this.loginRequest(username, password, setIsAuth);
      } else {
        throw new Error(`Error, Status code: ${response.status}`);
      }
    } catch (error) {
      console.log(`Error caught: ${error}`);
      throw error;
    }
  }

  async logoutRequest(setIsAuth) {
    try {
      const response = await axios.post(
        `${this.URL}/api/auth/logout`,
        {},
        { withCredentials: true },
      );

      if (response.status >= 200 && response.status < 300) {
        console.log("Logged out!");
        Cookies.remove("isAuth");
        setIsAuth(false);
      } else {
        throw new Error(`Error, status code: ${response.status}`);
      }
    } catch (error) {
      console.log(`Error caught: ${error}`);
    }
  }
}

export default Routes;
