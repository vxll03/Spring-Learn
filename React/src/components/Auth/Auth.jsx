import Button from "../Button/Button"

const Auth = () => {
  return (
    <div>
      <h1>Login</h1>
      <label for="login">Логин</label>
      <input id="login" type="text" />

      <label for="password">Пароль</label>
      <input id="password" type="password" />

      <Button>Войти</Button>
    </div>
  );
};

export default Auth;
