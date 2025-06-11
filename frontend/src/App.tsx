import React from 'react';
import { Container, CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import UserList from './components/UserList';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container>
        <UserList />
      </Container>
    </ThemeProvider>
  );
}

export default App;
