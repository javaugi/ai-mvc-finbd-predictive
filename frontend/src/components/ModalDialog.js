import Modal from 'react-modal';

export default function MyModal() {
  const [modalIsOpen, setIsOpen] = useState(false);
  
  return (
    <>
      <button onClick={() => setIsOpen(true)}>Open Modal</button>
      <Modal isOpen={modalIsOpen} onRequestClose={() => setIsOpen(false)}>
        <h2>Hello!</h2>
        <button onClick={() => setIsOpen(false)}>Close</button>
      </Modal>
    </>
  );
}
