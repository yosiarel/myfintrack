import Swal from 'sweetalert2'

const swalGlassTheme = {
  background: 'rgba(255, 255, 255, 0.1)',
  backdrop: 'rgba(0, 0, 0, 0.4)',
  color: '#ffffff',
  confirmButtonColor: '#667eea',
  cancelButtonColor: '#6b7280',
  customClass: {
    popup: 'glass-swal',
    confirmButton: 'glass-button-primary',
    cancelButton: 'glass-button-secondary',
  },
}

export function useNotification() {
  const showSuccess = (message: string, title: string = 'Success!') => {
    return Swal.fire({
      ...swalGlassTheme,
      title,
      text: message,
      icon: 'success',
      timer: 3000,
      timerProgressBar: true,
      showConfirmButton: false,
    })
  }

  const showError = (message: string, title: string = 'Error!') => {
    return Swal.fire({
      ...swalGlassTheme,
      title,
      text: message,
      icon: 'error',
      confirmButtonColor: '#ef4444',
    })
  }

  const showConfirm = (title: string, text: string) => {
    return Swal.fire({
      ...swalGlassTheme,
      title,
      text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes',
      cancelButtonText: 'Cancel',
    })
  }

  const showInfo = (message: string, title: string = 'Info') => {
    return Swal.fire({
      ...swalGlassTheme,
      title,
      text: message,
      icon: 'info',
    })
  }

  const showLoading = (message: string = 'Processing...') => {
    return Swal.fire({
      ...swalGlassTheme,
      title: message,
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      didOpen: () => {
        Swal.showLoading()
      },
    })
  }

  const close = () => {
    Swal.close()
  }

  return {
    showSuccess,
    showError,
    showConfirm,
    showInfo,
    showLoading,
    close,
  }
}
