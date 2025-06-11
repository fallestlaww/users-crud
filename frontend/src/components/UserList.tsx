import React, { useEffect, useState } from 'react';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    TextField,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Box,
    Pagination,
    Snackbar,
    Alert
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { User } from '../types/User';
import { userApi } from '../services/api';

const UserList: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchTerm, setSearchTerm] = useState('');
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: ''
    });
    const [error, setError] = useState<string | null>(null);
    const [openError, setOpenError] = useState(false);

    const showError = (err: any) => {
        let message = 'Unknown error';
        let status = err?.response?.status;
        if (err?.response?.data?.message) {
            message = err.response.data.message;
        } else if (err?.message) {
            message = err.message;
        }
        if (status) {
            setError(`[${status}] ${message}`);
        } else {
            setError(message);
        }
        setOpenError(true);
    };

    const fetchUsers = async () => {
        try {
            console.log('Fetching users...');
            const response = await userApi.getAllUsers(page, 5);
            console.log('API Response:', response);
            setUsers(response.content || []);
            setTotalPages(response.totalPages || 0);
        } catch (error) {
            showError(error);
        }
    };

    const handleSearch = async () => {
        try {
            const response = await userApi.searchUsers(searchTerm, page, 5);
            setUsers(response.content || []);
            setTotalPages(response.totalPages || 0);
        } catch (error) {
            showError(error);
        }
    };

    const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
        setPage(value - 1);
    };

    const handleOpenDialog = (user?: User) => {
        if (user) {
            setSelectedUser(user);
            setFormData({
                firstName: user.firstName,
                lastName: user.lastName,
                email: user.email
            });
        } else {
            setSelectedUser(null);
            setFormData({
                firstName: '',
                lastName: '',
                email: ''
            });
        }
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedUser(null);
    };

    const handleSubmit = async () => {
        try {
            if (selectedUser) {
                await userApi.updateUser(selectedUser.id, formData);
            } else {
                await userApi.createUser(formData);
            }
            handleCloseDialog();
            fetchUsers();
        } catch (error) {
            showError(error);
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await userApi.deleteUser(id);
                fetchUsers();
            } catch (error) {
                showError(error);
            }
        }
    };

    useEffect(() => {
        fetchUsers();
    }, [page]);

    return (
        <Box sx={{ p: 3 }}>
            <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
                <TextField
                    label="Search by First Name"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <Button variant="contained" onClick={handleSearch}>
                    Search
                </Button>
                <Button variant="contained" color="primary" onClick={() => handleOpenDialog()}>
                    Add User
                </Button>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>First Name</TableCell>
                            <TableCell>Last Name</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell>{user.id}</TableCell>
                                <TableCell>{user.firstName}</TableCell>
                                <TableCell>{user.lastName}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>
                                    <IconButton onClick={() => handleOpenDialog(user)}>
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton onClick={() => handleDelete(user.id)}>
                                        <DeleteIcon />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                <Pagination
                    count={totalPages}
                    page={page + 1}
                    onChange={handlePageChange}
                    color="primary"
                />
            </Box>

            <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>{selectedUser ? 'Edit User' : 'Add User'}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
                        <TextField
                            label="First Name"
                            value={formData.firstName}
                            onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                        />
                        <TextField
                            label="Last Name"
                            value={formData.lastName}
                            onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                        />
                        <TextField
                            label="Email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog}>Cancel</Button>
                    <Button onClick={handleSubmit} variant="contained" color="primary">
                        {selectedUser ? 'Update' : 'Create'}
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar open={openError} autoHideDuration={6000} onClose={() => setOpenError(false)}>
                <Alert onClose={() => setOpenError(false)} severity="error" sx={{ width: '100%' }}>
                    {error}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default UserList; 