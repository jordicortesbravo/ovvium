import * as React from 'react';
import { Row } from 'react-bootstrap';
import { Input } from '../Input/Input';
import { Col } from 'react-bootstrap';
import { Button } from '../Button/Button';
import { connect } from 'react-redux';
import * as style from './style.css';
import { browserHistory } from './../../App';

interface RecoverFormProps {
  onRecover: (email: string) => void;
  error: boolean;
  showIndicator: boolean;
}

interface RecoverFormState {
  email: string;
  sent: boolean;
  isLoading: false;
}

export class RecoverForm extends React.Component<RecoverFormProps, RecoverFormState> {

  constructor(props: RecoverFormProps) {
    super(props);
    this.state = {
      email: "",
      sent: false
    } as RecoverFormState;
  }

  handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    // TODO: Validate input data
  }

  onClick() {
    this.props.onRecover(this.state.email);
    this.setState({ sent: true });
  }

  onChangeEmail(value: string) {
    this.setState({ email: value });
  }

  back() {
    browserHistory.goBack();
  }


  render() {
    return (
      <div className="row justify-content-center align-items-center h-100">
        <form
          className={style.box + ' col-lg-5 col-md-5 col-sm-8'}
          onSubmit={this.handleSubmit.bind(this)}
        >
          <Row>
            <Col>
              <span className={style.accessTitle}>¿Has olvidado tu contraseña?</span>
              <p className={style.subtitle}>Te enviaremos un mail para recuperar tu cuenta.</p>
            </Col>
          </Row>
          <Row>
            <Col>
              <Input
                type="text"
                inputMode="email"
                name="username"
                placeholder="Email"
                onChange={this.onChangeEmail.bind(this)}
                keyboardShown={false}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <Button block value={this.props.showIndicator ? 'ESPERA...' : 'ENVIAR'} className='w-100' onClick={this.onClick.bind(this)} />
            </Col>
          </Row>
          {this.props.error && (
            <Row>
              <Col>
                <p className={style.error}>Ha ocurrido un error, vuelve a introducir los datos.</p>
              </Col>
            </Row>
          )}
          {this.state.sent && (
            <Row>
              <Col>
                <p className={style.subtitle}>Te hemos enviado un correo a tu cuenta.</p>
              </Col>
            </Row>
          )}
          <Row>
            <Col className={style.back}>
              <div onClick={this.back.bind(this)}><span>Volver</span></div>
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
)(RecoverForm);
