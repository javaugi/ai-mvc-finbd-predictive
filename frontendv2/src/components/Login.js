import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const [credentials, setCredentials] = useState({username: '', password: ''});

    const handleOAuthLogin = (provider) => {
        // Open OAuth in same window
        window.location.href = `http://localhost:8088/oauth2/authorization/${provider}?redirect_uri=http://localhost:3000/dashboard`;
    };

    const handleOAuthLoginEpic = (provider) => {
        // Open OAuth in same window
        window.location.href = `http://localhost:8088/oauth2/authorization/${provider}`;
    };

    const handleInternalLogin = async (e) => {
        e.preventDefault();
        try {
            const {username, password} = credentials;
            const resp = await axios.post("/auth/login", {
                username,
                password,
            });
            const {token} = resp.data;
            localStorage.setItem("token", token);
            axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
            navigate('/dashboard');
        } catch (error) {
            console.error("Login error:", error);
            setError("Login failed. Please check your credentials and try again.");
        }
    };

    return (
            <div className="login-container">
                <div className="login-box">
                    <h1 className="login-title">Welcome to OAuth Demo</h1>

                    <div className="oauth-buttons">
                        <button 
                            className="oauth-btn google-btn"
                            onClick={() => handleOAuthLogin('google')}
                            >
                            🔗 Login with Google
                        </button>
            
                        <button 
                            className="oauth-btn github-btn"
                            onClick={() => handleOAuthLogin('github')}
                            >
                            💻 Login with GitHub
                        </button>

                        <button 
                            className="oauth-btn epicfhir-btn"
                            onClick={() => handleOAuthLoginEpic('epicfhir')}
                            >
                            💻 Login with Epis on Fhir
                        </button>
                    </div>

                    <hr style={{margin: '1.5rem 0'}} />
            
                    <form onSubmit={handleInternalLogin}>
                        <div style={{marginBottom: '1rem'}}>
                            <input
                                type="text"
                                placeholder="Username or email"
                                value={credentials.username}
                                onChange={(e) => setCredentials({...credentials, username: e.target.value})}
                                style={{width: '100%', padding: '8px', marginBottom: '10px'}}
                                />
                            <input
                                type="password"
                                placeholder="Password"
                                value={credentials.password}
                                onChange={(e) => setCredentials({...credentials, password: e.target.value})}
                                style={{width: '100%', padding: '8px'}}
                                />
                        </div>
                        <button type="submit" className="oauth-btn internal-btn" style={{width: '100%'}}>
                            🔐 Internal Login (Demo)
                        </button>
                    </form>

                    {error && <div className="error">{error}</div>}

                    <div style={{marginTop: '1rem', fontSize: '12px', color: '#999'}}>
                        <p>Internal login is for demo purposes only</p>
                    </div>
                </div>
            </div>
            );
};

export default Login;