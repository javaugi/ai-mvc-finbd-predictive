import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

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

    const handleInternalLogin = async (e) => {
        e.preventDefault();
        // For demo purposes - in real app, you'd call your backend
        alert('Internal login would be implemented here');
        try {
            const response = await axios.post("http://localhost:8088/auth/login", {
                email: username,
                password: password,
            });
            localStorage.setItem("token", response.data);
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
                    </div>
            
                    <hr style={{margin: '1.5rem 0'}} />
            
                    <form onSubmit={handleInternalLogin}>
                        <div style={{marginBottom: '1rem'}}>
                            <input
                                type="text"
                                placeholder="Username"
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
                    <div style={{marginTop: '1rem', fontSize: '12px', color: '#999'}}>
                        <p>Internal login is for demo purposes only</p>
                    </div>
                </div>
            </div>
            );
};

export default Login;