import * as React from 'react';
import * as style from './style.css';
import { Row, Col } from 'react-bootstrap';

export class Loading extends React.Component<any> {
  
  render() {

    return  <div className="row justify-content-center align-items-center h-100">          
                <Row>
                    <Col style={{padding: 0}}>
                        <svg width="70px" height="70px" viewBox="0 0 100 100" preserveAspectRatio="xMidYMid">
                            <circle cx="50" cy="50" fill="none" stroke="#f5b800" strokeWidth="10" r="35" strokeDasharray="164.93361431346415 56.97787143782138" transform="rotate(250.487 50 50)">
                                <animateTransform attributeName="transform" type="rotate" repeatCount="indefinite" dur="1s" values="0 50 50;360 50 50" keyTimes="0;1"></animateTransform>
                            </circle>
                        </svg>
                    </Col>
                    <Col className={style.loadingText}>
                        <span>Cargando...</span>
                    </Col>
                </Row>
            </div>
  }
}
