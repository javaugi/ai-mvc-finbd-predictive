import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useSearchParams  } from 'react-router-dom';

export default function Dashboard() {
    const [user, setUser] = useState(null);
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const token = params.get("token");
        const username = params.get("username");
        const apiKey = params.get("X-API-KEY");
        const clientId = params.get("X-User-Id");
        if (token) {
            localStorage.setItem("token", token);
            localStorage.setItem("username", username);
            localStorage.setItem("apiKey", apiKey);
            localStorage.setItem("userId", clientId);
            window.history.replaceState({}, document.title, "/dashboard");
        }

        const jwt = localStorage.getItem("token");
        const xuname = localStorage.getItem("username");
        const xkey = localStorage.getItem("apiKey");
        const xuid = localStorage.getItem("userId");
        if (!jwt) {
            navigate("/login");
            return;
        }

        axios.get("http://localhost:8088/auth/check", {
            headers: {
                Authorization: `Bearer ${jwt}`,
                "X-API-KEY": xkey,
                "X-User-Id": xuid,
                "X-User-Name": xuname
            },
        }).then((response) => {
                    if (response.data.authenticated) {
                        setUser(response.data.user);
                    } else {
                        navigate("/login");
                    }
                })
                .catch((error) => {
                    console.error("Auth check failed", error);
                    navigate("/login");
                })
                .finally(() => setLoading(false));
    }, [navigate]);


    const checkAuthentication = async () => {
        try {
            const params = new URLSearchParams(window.location.search);
            const token = params.get("token");

            if (token) {
                // store token securely
                localStorage.setItem("token", token);

                // clean up URL (remove ?token=...)
                window.history.replaceState({}, document.title, "/dashboard");
            }

            // if no token in URL or storage, go back to login
            else if (!localStorage.getItem("token")) {
                navigate("/login");
            }

            token = localStorage.getItem("token");
            // Include token in request if present in URL
            const config = {
                withCredentials: true, // Important for session cookies
                params: token ? {token} : {}
            };

            const response = await axios.get("http://localhost:8088/auth/check", {
                headers: {Authorization: `Bearer ${token}`},
            });

            if (response.data.authenticated) {
                setUser(response.data.user);
                setLoading(false);
            } else {
                navigate('/login');
            }
        } catch (error) {
            console.error('Authentication check failed:', error);
            navigate('/login');
        } finally {
            setLoading(false);
        }
    };
    const handleLogout = async () => {
        try {
            await axios.post('/auth/logout', {}, {
                withCredentials: true
            });
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            setUser(null);
            navigate('/login?logout=success');
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!user) {
        return (
                <div className="dashboard">
                    <div className="dashboard-header">
                        <h2>Not Authenticated</h2>
                        <button onClick={() => navigate('/login')}>Go to Login</button>
                    </div>
                </div>
                );
    }

    return (
            <div className="dashboard">
                            <div className="dashboard-header">
                                <h1>Dashboard</h1>
                                {user && (
                                    <>
                                    <p>Welcome, {user.name}!</p>
                                    <p>Email: {user.email}</p>
                                    <button className="logout-btn" onClick={handleLogout}>
                                        Logout
                                    </button>
                                    </>
                                )}
                            </div>
            
                            {/* Dashboard content */}
                            <div className="links-container">
                                <a href="http://localhost:8088/actuator" target="_blank" rel="noopener noreferrer" className="link-card">
                                    <h3>Spring Boot Actuator</h3>
                                    <p>Monitor and manage your application</p>
                                </a>
            
                                <a href="http://localhost:8088/swagger-ui/index.html" target="_blank" rel="noopener noreferrer" className="link-card">
                                    <h3>Swagger UI</h3>
                                    <p>API documentation and testing</p>
                                </a>

                                {dashboardData && (
                            <div className="link-card">
                                <h3>Backend Info</h3>
                                <p><strong>Message:</strong> {dashboardData.message}</p>
                                <p><strong>Method:</strong> {dashboardData.authenticationMethod || 'Session'}</p>
                            </div>
                        )}
                            </div>
                        </div>
            );
}

