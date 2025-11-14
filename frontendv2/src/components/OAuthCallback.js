import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const OAuthCallback = () => {
    const navigate = useNavigate();

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            // Give the backend a moment to process the OAuth callback
            setTimeout(async () => {
                try {
                    const token = localStorage.getItem("token");
                    const response = await axios.get("http://localhost:8088/auth/check", {
                        headers: {Authorization: `Bearer ${token}`},
                    });

                    if (response.data.authenticated) {
                        navigate('/dashboard');
                    } else {
                        navigate('/login');
                    }
                } catch (error) {
                    console.error('Auth check failed:', error);
                    navigate('/login');
                }
            }, 1000);
        } catch (error) {
            console.error('OAuth callback error:', error);
            navigate('/login');
        }
    };

    return (
            <div className="login-container">
                <div className="login-box">
                    <h2>Completing authentication...</h2>
                    <p>Please wait while we redirect you.</p>
                </div>
            </div>
            );
};

export default OAuthCallback;
