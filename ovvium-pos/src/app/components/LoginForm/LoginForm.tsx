import * as React from 'react';
import { Row } from 'react-bootstrap';
import { Input } from '../Input/Input';
import { Col } from 'react-bootstrap';
import { Button } from '../Button/Button';
import { connect } from 'react-redux';
import * as style from './style.css';

interface LoginFormProps {
  onLogin: (email: string, password: string) => void;
  onRecover: () => void;
  error: boolean;
  showIndicator: boolean;
}

interface LoginFormState {
  username: string;
  password: string;
  isLoading: false;
}

export class LoginForm extends React.Component<LoginFormProps, LoginFormState> {
  constructor(props: LoginFormProps) {
    super(props);
    this.state = {
      username : "",
      password: ""
    } as LoginFormState;
  }

  handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    // TODO: Validate input data
  }

  onClick() {
    this.props.onLogin(this.state.username, this.state.password);
  }

  onRecover() {
    this.props.onRecover();
  }

  onChangeEmail(value: string) {
    this.setState({ username: value });
  }

  onChangePassword(value: string) {
    this.setState({ password: value });
  }

  render() {
    return (
        <div className="row justify-content-center align-items-center h-100">          
          <form
            className={style.box + ' col-lg-5 col-md-6 col-sm-8'}
            onSubmit={this.handleSubmit.bind(this)}
          >
            <Row>
              <Col>
                <span className={style.accessTitle}>Accede a tu cuenta</span>
              </Col>
            </Row>
            <Row>
              <Col>
                <Input
                  type="text"
                  name="username"
                  inputMode="email"
                  placeholder="Email"
                  onChange={this.onChangeEmail.bind(this)}
                  keyboardShown={false}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <Input
                  type="password"
                  name="password"
                  placeholder="Contraseña"
                  onChange={this.onChangePassword.bind(this)}
                  keyboardShown={false}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <Button block value={this.props.showIndicator ? 'ESPERA...' : 'ACCEDER'} className='w-100' onClick={this.onClick.bind(this)} />
              </Col>
            </Row>
            {this.props.error && (
              <Row>
                <Col>
                  <p className={style.error}>Ha ocurrido un error, vuelve a introducir los datos.</p>
                </Col>
              </Row>
            )}
            <Row>
              <Col className={style.forgotPassword}>
                <div onClick={this.onRecover.bind(this)}><span>He olvidado la contraseña</span></div>
              </Col>
            </Row>
          </form>
        </div>
    );
  }
}

export default connect(
  null,
  {}
)(LoginForm);
