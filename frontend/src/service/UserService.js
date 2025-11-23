import { useContext, useEffect, useState } from 'react';

function UserList() {
    const [users, setUsers] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const fetchUsers = async (page) => {
        const response = await fetch(`/api/users?page=${page}&size=20`);
        const data = await response.json();
        setUsers(data.content);
        setTotalPages(data.totalPages);
    };

    return (
            <div>
                {/* Display users */}
                <Pagination 
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={fetchUsers}
                />
            </div>
            );
}
