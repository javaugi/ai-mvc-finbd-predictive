// src/tables/UserList.js
import { useContext, useEffect, useState } from 'react';
import axios from '../api/axiosConfig';
import { AuthContext } from '../auth/AuthContext';

const UserList = () => {
  const [users, setUsers] = useState([]);
  const { logout } = useContext(AuthContext);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const { data } = await axios.get('/api/user');
        setUsers(data);
      } catch (err) {
        console.error('Failed to fetch users:', err);
      }
    };
    fetchUsers();
  }, []);

  return (
    <main role="main">
      <h1>Users</h1>
      <ul>
        {users.map(user => (
          <li key={user.id}>{user.name} - {user.email}</li>
        ))}
      </ul>
      <div>
        <button onClick={logout}>Logout</button>
        {/* Render users */}
      </div>
    </main>      
  );
};

export default UserList;
