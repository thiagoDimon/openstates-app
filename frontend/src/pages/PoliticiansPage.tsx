import { useState } from 'react'
import { Box, Button, CircularProgress, Container, IconButton, Typography } from '@mui/material'
import SearchIcon from '@mui/icons-material/Search'
import SyncIcon from '@mui/icons-material/Sync'
import ClearIcon from '@mui/icons-material/Clear'
import { usePoliticians } from '@/hooks/usePoliticians'
import { FilterBar } from '@/components/FilterBar'
import { PoliticianGrid } from '@/components/PoliticianGrid'
import { SyncDataModal } from '@/components/SyncDataModal'

export function PoliticiansPage() {
  const [state, setState] = useState('')
  const [party, setParty] = useState('')
  const [appliedState, setAppliedState] = useState<string | undefined>(undefined)
  const [appliedParty, setAppliedParty] = useState<string | undefined>(undefined)
  const [syncModalOpen, setSyncModalOpen] = useState(false)

  const { data, isLoading, isError, error, isFetched, fetchNextPage, hasNextPage, isFetchingNextPage } = usePoliticians(
    appliedState,
    appliedParty
  )

  const politicians = data?.pages.flatMap((p) => p.content) ?? []
  const errorMessage = isError && error instanceof Error ? error.message : undefined

  function handleSearch() {
    setAppliedState(state || undefined)
    setAppliedParty(party || undefined)
  }

  function handleClearFilters() {
    setState('')
    setParty('')
    setAppliedState(undefined)
    setAppliedParty(undefined)
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
          US Politicians
        </Typography>
        <IconButton
          onClick={() => setSyncModalOpen(true)}
          sx={{
            display: { xs: 'flex', md: 'none' },
            bgcolor: '#111',
            color: 'white',
            '&:hover': { bgcolor: '#333' },
          }}
        >
          <SyncIcon />
        </IconButton>
      </Box>

      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: 2,
          mb: 3,
        }}
      >
        <Box sx={{ order: { xs: 1, sm: 2 }, display: 'flex', gap: 2, ml: 'auto' }}>
          {appliedState && (
            <Button
              variant="outlined"
              size="large"
              onClick={handleClearFilters}
              startIcon={<ClearIcon />}
              sx={{
                borderRadius: '50px',
                textTransform: 'none',
                px: 3,
                color: 'text.secondary',
                borderColor: 'divider',
                '&:hover': { borderColor: 'text.secondary', bgcolor: 'action.hover' },
              }}
            >
              Clear Filters
            </Button>
          )}

          <Button
            variant="contained"
            size="large"
            onClick={handleSearch}
            disabled={isLoading || !state}
            startIcon={<SearchIcon />}
            sx={{ borderRadius: '50px', textTransform: 'none', px: 3 }}
          >
            Search
          </Button>

          <Button
            variant="contained"
            size="large"
            onClick={() => setSyncModalOpen(true)}
            startIcon={<SyncIcon />}
            sx={{
              display: { xs: 'none', md: 'flex' },
              borderRadius: '50px',
              bgcolor: '#111',
              '&:hover': { bgcolor: '#333' },
              textTransform: 'none',
              px: 3,
            }}
          >
            Sync Data
          </Button>
        </Box>

        <Box sx={{ order: { xs: 2, sm: 1 }, width: { xs: '100%', sm: 'auto' } }}>
          <FilterBar state={state} party={party} onStateChange={setState} onPartyChange={setParty} />
        </Box>
      </Box>

      {isLoading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
          <CircularProgress />
        </Box>
      )}

      {!isLoading && !isFetched && (
        <Typography color="text.secondary" sx={{ mt: 4, textAlign: 'center' }}>
          Select a state to list the politicians and click Search.
        </Typography>
      )}

      {!isLoading && isFetched && (
        <PoliticianGrid
          politicians={politicians}
          isError={isError}
          errorMessage={errorMessage}
          onLoadMore={fetchNextPage}
          hasMore={hasNextPage ?? false}
          isLoadingMore={isFetchingNextPage}
        />
      )}

      <SyncDataModal open={syncModalOpen} onClose={() => setSyncModalOpen(false)} />
    </Container>
  )
}
